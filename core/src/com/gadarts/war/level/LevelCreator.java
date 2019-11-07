package com.gadarts.war.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gadarts.shared.SharedC;
import com.gadarts.shared.SharedC.Level;
import com.gadarts.shared.level.LevelModeler;
import com.gadarts.shared.level.Map;
import com.gadarts.shared.level.TilePathSignature;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC;
import com.gadarts.war.GameSettings;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.GroundComponent;
import com.gadarts.war.components.model.ModelInstanceComponent;
import com.gadarts.war.components.physics.PhysicsComponent;
import com.gadarts.war.systems.physics.PhysicsSystem;

import java.util.HashMap;

public class LevelCreator extends LevelModeler {
    private static Vector3 auxVector31 = new Vector3();
    private static Matrix4 auxMatrix = new Matrix4();
    private HashMap<String, Material> materials = new HashMap<>();

    public LevelCreator(PooledEngine entitiesEngine) {
        super(new ModelBuilder(), entitiesEngine);
    }

    private void createGroundBody(Map map) {
        PooledEngine entitiesEngine = getEntitiesEngine();
        ImmutableArray<Entity> groundRegions = entitiesEngine.getEntitiesFor(Family.all(GroundComponent.class).get());
        for (Entity entity : groundRegions) {
            if (ComponentsMapper.ground.get(entity).isPhysical()) {
                createGroundRegionBody(entity, entitiesEngine, map);
            }
        }
    }

    private void createGroundRegionBody(Entity entity, PooledEngine engine, Map map) {
        GroundComponent groundComponent = ComponentsMapper.ground.get(entity);
        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        ModelInstanceComponent modelInstanceComponent = ComponentsMapper.modelInstance.get(entity);
        ModelInstance modelInstance = modelInstanceComponent.getModelInstance();
        int halfWidth = Level.REGION_SIZE_UNIT;
        btCompoundShape compoundShape = new btCompoundShape(true);
        Matrix4 transform = modelInstance.transform;
        physicsComponent.init(0, compoundShape, transform);
        btRigidBody body = physicsComponent.getBody();
        for (int row = 0; row < Level.REGION_SIZE_UNIT; row++) {
            for (int col = 0; col < Level.REGION_SIZE_UNIT; col++) {
                int originX = (int) transform.val[Matrix4.M03];
                int originZ = (int) transform.val[Matrix4.M23];
                compoundShape.addChildShape(auxMatrix.idt().trn(col + 0.5f, 0, row + 0.5f), new GroundChildShape(auxVector31.set(0.5f, 0.1f, 0.5f),map.getPath()[originZ + row][originX + col]));
                groundComponent.getFrictionMapping()[row][col] = map.getPath()[originZ + row][originX + col];
            }
        }
        entity.add(physicsComponent);
        body.userData = entity;
        body.setContactCallbackFlag(btBroadphaseProxy.CollisionFilterGroups.KinematicFilter);
        engine.getSystem(PhysicsSystem.class).getCollisionWorld().addRigidBody(body);
    }

    private GroundBody createBoundaryPhysics(Vector3 planeNormal, float distanceFromOrigin) {
        GroundBody boundaryBody = new GroundBody();
        btRigidBody btRigidBody = createBoundaryRigidBody(planeNormal, boundaryBody, distanceFromOrigin);
        boundaryBody.setBody(btRigidBody);
        PooledEngine engine = getEntitiesEngine();
        engine.getSystem(PhysicsSystem.class).getCollisionWorld().addRigidBody((btRigidBody) boundaryBody.getBody());
        return boundaryBody;
    }

    private btRigidBody createBoundaryRigidBody(Vector3 planeNormal, GroundBody boundaryBody, float distanceFromOrigin) {
        btStaticPlaneShape collisionShape = new btStaticPlaneShape(planeNormal, distanceFromOrigin);
        boundaryBody.setBodyInfo(new btRigidBody.btRigidBodyConstructionInfo(0, null, collisionShape));
        btRigidBody btRigidBody = new btRigidBody(boundaryBody.getBodyInfo());
        btRigidBody.setContactCallbackFlag(btBroadphaseProxy.CollisionFilterGroups.KinematicFilter);
        return btRigidBody;
    }


    public void createLevelPhysics(Map map) {
        createGroundBody(map);
        int distanceFromOrigin = Level.REGION_SIZE_UNIT * Level.LEVEL_SIZE;
        createBoundaryPhysics(auxVector31.set(0, 0, 1), 0);
        createBoundaryPhysics(auxVector31.set(1, 0, 0), 0);
        createBoundaryPhysics(auxVector31.set(0, 0, -1), -distanceFromOrigin);
        createBoundaryPhysics(auxVector31.set(-1, 0, 0), -distanceFromOrigin);
    }

    public void modelLevelGround(Map map, TextureAtlas tilesAtlas) {
        for (int row = 0; row < Level.LEVEL_SIZE; row++) {
            for (int col = 0; col < Level.LEVEL_SIZE; col++) {
                modelGroundRegion(map, tilesAtlas, row, col);
            }
        }
        if (!GameSettings.DRAWING_SKIPPING_MODE || !GameSettings.SKIP_DRAWING_SURROUNDING_TERRAIN) {
            modelSurroundingGround();
        }
    }

    private void modelGroundRegion(Map map, TextureAtlas tilesAtlas, int originRow, int originCol) {
        builder.begin();
        int originRowUnits = originRow * Level.REGION_SIZE_UNIT;
        int originColUnits = originCol * Level.REGION_SIZE_UNIT;
        for (int row = 0; row < Level.REGION_SIZE_UNIT; row++) {
            for (int col = 0; col < Level.REGION_SIZE_UNIT; col++) {
                Material material = getMaterial(map, originRowUnits + row, originColUnits + col, tilesAtlas);
                MeshPartBuilder meshBuilder = builder.part("ground", GL20.GL_TRIANGLES,
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                                VertexAttributes.Usage.TextureCoordinates, material);
                meshBuilder.setUVRange(0, 0, 1, 1);
                normal.set(0, 1, 0);
                meshBuilder.rect(corner00.set(col, 0, row + 1), corner10.set(col + 1, 0, row + 1),
                        corner11.set(col + 1, 0, row), corner01.set(col, 0, row), normal);
            }
        }
        Model groundModel = builder.end();
        Entity ground = new Entity();
        PooledEngine engine = getEntitiesEngine();
        ModelInstanceComponent modelInstanceComponent = engine.createComponent(ModelInstanceComponent.class);
        ModelInstance modelInstance = new ModelInstance(groundModel);
        modelInstance.transform.setToTranslation(auxVector31.set(originColUnits, 0, originRowUnits));
        modelInstanceComponent.init(modelInstance);
        ground.add(modelInstanceComponent);
        GroundComponent groundComponent = engine.createComponent(GroundComponent.class);
        groundComponent.init(true);
        ground.add(groundComponent);
        engine.addEntity(ground);
    }

    private Material getMaterial(Map map, int row, int col, TextureAtlas tilesAtlas) {
        int sig = calculateSurroundPathSignature(map.getPath(), row, col);
        String name = tilesAtlas.findRegion(TilePathSignature.findByValue(sig).getName()).name;
        if (materials.containsKey(name)) return materials.get(name);
        else {
            Material material = createGroundMaterial(tilesAtlas, name);
            materials.put(name, material);
            return material;
        }
    }

    private void modelSurroundingGround() {
        modelHorizontalSurroundingGroundModel(Level.LEVEL_SIZE + 2,
                -1 * Level.REGION_SIZE_UNIT * 2);
        modelHorizontalSurroundingGroundModel(Level.LEVEL_SIZE + 2,
                Level.LEVEL_SIZE * Level.REGION_SIZE_UNIT);
        modelVerticalSurroundingGroundModel(-1);
        modelVerticalSurroundingGroundModel(1);
    }

    private void modelVerticalSurroundingGroundModel(int x) {
        for (int i = 0; i < Level.LEVEL_SIZE; i++) {
            int regionSize = Level.REGION_SIZE_UNIT * 2;
            modelGroundRegion(i * regionSize, x, regionSize, true);
        }
    }

    private void modelHorizontalSurroundingGroundModel(int numberOfRegions, int z) {
        for (int i = 0; i < numberOfRegions; i++) {
            int regionSize = Level.REGION_SIZE_UNIT * 2;
            modelGroundRegion(z, i - 1, regionSize, true);
        }
    }

    private void modelGroundRegion(int z, int x, int regionSize) {
        modelGroundRegion(z, x, regionSize, false);
    }

    private void modelGroundRegion(int z, int x, int regionSize, boolean isSurrounding) {
        Entity ground = new Entity();
        String grass = GameC.Files.TEXTURES_FOLDER_NAME + "/" + SharedC.TILE_FILE_NAME;
        TextureAtlas tiles = GameAssetManager.getInstance().get(grass, TextureAtlas.class);
        boolean modelAxis = Gdx.app.getLogLevel() == Gdx.app.LOG_DEBUG && GameSettings.SHOW_AXIS && z == 0 && x == 0;
        Model region = modelGroundRegion(modelAxis, tiles, regionSize);
        PooledEngine entitiesEngine = getEntitiesEngine();
        ground.add(createGroundRegionModelInstanceComponent(z, x, region, regionSize, entitiesEngine));
        GroundComponent groundComponent = entitiesEngine.createComponent(GroundComponent.class);
        groundComponent.init(!isSurrounding);
        ground.add(groundComponent);
        entitiesEngine.addEntity(ground);
    }

    private ModelInstanceComponent createGroundRegionModelInstanceComponent(int z, int x, Model regionModel,
                                                                            int regionSize, PooledEngine engine) {
        ModelInstanceComponent modelInstanceComponent = engine.createComponent(ModelInstanceComponent.class);
        ModelInstance modelInstance = new ModelInstance(regionModel);
        x = x * regionSize;
        int y = 0;
        modelInstance.transform.setToTranslation(x, y, z);
        modelInstanceComponent.init(modelInstance);
        return modelInstanceComponent;
    }

    public void createLevelIntoEngine(Map map) {
        TextureAtlas tilesAtlas = GameAssetManager.getInstance().get(GameC.Files.TEXTURES_FOLDER_NAME + "/" + "grass.txt", TextureAtlas.class);
        modelLevelGround(map, tilesAtlas);
        createLevelPhysics(map);
    }

}
