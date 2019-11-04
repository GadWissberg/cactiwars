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
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
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

    public LevelCreator() {
        super(new ModelBuilder());
    }

    private void createGroundBody(PooledEngine engine) {
        ImmutableArray<Entity> groundRegions = engine.getEntitiesFor(Family.all(GroundComponent.class).get());
        for (Entity entity : groundRegions) {
            if (ComponentsMapper.ground.get(entity).isPhysical()) {
                createGroundRegionBody(entity, engine);
            }
        }
    }

    private void createGroundRegionBody(Entity entity, PooledEngine engine) {
        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        ModelInstanceComponent modelInstanceComponent = ComponentsMapper.modelInstance.get(entity);
        ModelInstance modelInstance = modelInstanceComponent.getModelInstance();
        int halfWidth = Level.REGION_SIZE_UNIT;
        btCompoundShape compoundShape = new btCompoundShape(true);
        for (int i = 0; i < Level.REGION_SIZE_UNIT; i++) {
            for (int j = 0; j < Level.REGION_SIZE_UNIT; j++) {
                compoundShape.addChildShape(auxMatrix.idt().trn(j + 0.5f, 0, i + 0.5f), new btBoxShape(auxVector31.set(0.5f, 0.1f, 0.5f)));
            }
        }
        physicsComponent.init(0, compoundShape, modelInstance.transform);
        entity.add(physicsComponent);
        btRigidBody body = physicsComponent.getBody();
        body.userData = entity;
        body.setContactCallbackFlag(btBroadphaseProxy.CollisionFilterGroups.KinematicFilter);
        engine.getSystem(PhysicsSystem.class).getCollisionWorld().addRigidBody(body);
    }

    private GroundBody createBoundaryPhysics(Vector3 planeNormal, float distanceFromOrigin, PooledEngine engine) {
        GroundBody boundaryBody = new GroundBody();
        btRigidBody btRigidBody = createBoundaryRigidBody(planeNormal, boundaryBody, distanceFromOrigin);
        boundaryBody.setBody(btRigidBody);
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


    public void createLevelPhysics(PooledEngine engine) {
        createGroundBody(engine);
        int distanceFromOrigin = Level.REGION_SIZE_UNIT * Level.LEVEL_SIZE;
        createBoundaryPhysics(auxVector31.set(0, 0, 1), 0, engine);
        createBoundaryPhysics(auxVector31.set(1, 0, 0), 0, engine);
        createBoundaryPhysics(auxVector31.set(0, 0, -1), -distanceFromOrigin, engine);
        createBoundaryPhysics(auxVector31.set(-1, 0, 0), -distanceFromOrigin, engine);
    }

    public void modelLevelGround(PooledEngine engine, Map map, TextureAtlas tilesAtlas) {
        for (int row = 0; row < Level.LEVEL_SIZE; row++) {
            for (int col = 0; col < Level.LEVEL_SIZE; col++) {
                modelGroundRegion(engine, map, tilesAtlas, row, col);
            }
        }
        if (!GameSettings.DRAWING_SKIPPING_MODE || !GameSettings.SKIP_DRAWING_SURROUNDING_TERRAIN) {
            modelSurroundingGround(engine);
        }
    }

    private void modelGroundRegion(PooledEngine engine, Map map, TextureAtlas tilesAtlas, int originRow, int originCol) {
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

    private void modelSurroundingGround(PooledEngine engine) {
        modelHorizontalSurroundingGroundModel(Level.LEVEL_SIZE + 2,
                -1 * Level.REGION_SIZE_UNIT * 2, engine);
        modelHorizontalSurroundingGroundModel(Level.LEVEL_SIZE + 2,
                Level.LEVEL_SIZE * Level.REGION_SIZE_UNIT, engine);
        modelVerticalSurroundingGroundModel(-1, engine);
        modelVerticalSurroundingGroundModel(1, engine);
    }

    private void modelVerticalSurroundingGroundModel(int x, PooledEngine engine) {
        for (int i = 0; i < Level.LEVEL_SIZE; i++) {
            int regionSize = Level.REGION_SIZE_UNIT * 2;
            modelGroundRegion(i * regionSize, x, regionSize, engine, true);
        }
    }

    private void modelHorizontalSurroundingGroundModel(int numberOfRegions, int z, PooledEngine engine) {
        for (int i = 0; i < numberOfRegions; i++) {
            int regionSize = Level.REGION_SIZE_UNIT * 2;
            modelGroundRegion(z, i - 1, regionSize, engine, true);
        }
    }

    private void modelGroundRegion(int z, int x, int regionSize, PooledEngine engine) {
        modelGroundRegion(z, x, regionSize, engine, false);
    }

    private void modelGroundRegion(int z, int x, int regionSize, PooledEngine engine, boolean isSurrounding) {
        Entity ground = new Entity();
        String grass = GameC.Files.TEXTURES_FOLDER_NAME + "/" + SharedC.TILE_FILE_NAME;
        TextureAtlas tiles = GameAssetManager.getInstance().get(grass, TextureAtlas.class);
        boolean modelAxis = Gdx.app.getLogLevel() == Gdx.app.LOG_DEBUG && GameSettings.SHOW_AXIS && z == 0 && x == 0;
        Model region = modelGroundRegion(modelAxis, tiles, regionSize);
        ground.add(createGroundRegionModelInstanceComponent(z, x, region, regionSize, engine));
        GroundComponent groundComponent = engine.createComponent(GroundComponent.class);
        groundComponent.init(!isSurrounding);
        ground.add(groundComponent);
        engine.addEntity(ground);
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

    public void createLevelIntoEngine(PooledEngine entitiesEngine, Map map) {
        TextureAtlas tilesAtlas = GameAssetManager.getInstance().get(GameC.Files.TEXTURES_FOLDER_NAME + "/" + "grass.txt", TextureAtlas.class);
        modelLevelGround(entitiesEngine, map, tilesAtlas);
        createLevelPhysics(entitiesEngine);
    }
}
