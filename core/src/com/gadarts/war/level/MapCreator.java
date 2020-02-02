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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.gadarts.shared.SharedC;
import com.gadarts.shared.SharedUtils;
import com.gadarts.shared.definitions.*;
import com.gadarts.shared.level.Map;
import com.gadarts.shared.level.MapModeler;
import com.gadarts.shared.level.PlacedActorInfo;
import com.gadarts.shared.par.SectionType;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.GroundComponent;
import com.gadarts.war.components.model.ModelInstanceComponent;
import com.gadarts.war.components.physics.PhysicsComponent;
import com.gadarts.war.factories.ActorFactory;
import com.gadarts.war.factories.PlayerProperties;
import com.gadarts.war.systems.CameraSystem;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystem;
import com.gadarts.war.systems.render.RenderSettings;

import java.util.HashMap;
import java.util.List;

public class MapCreator extends MapModeler {
    private static Vector3 auxVector31 = new Vector3();
    private static Matrix4 auxMatrix = new Matrix4();
    private HashMap<String, Material> materials = new HashMap<>();

    public MapCreator(PooledEngine entitiesEngine) {
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
        PhysicsComponent physicsComponent = createGroundRegionBodyPhysicsComponent(entity, engine, map);
        entity.add(physicsComponent);
        btRigidBody body = physicsComponent.getBody();
        body.userData = entity;
        body.setContactCallbackFlag(btBroadphaseProxy.CollisionFilterGroups.KinematicFilter);
        engine.getSystem(PhysicsSystem.class).getCollisionWorld().addRigidBody(body);
    }

    private PhysicsComponent createGroundRegionBodyPhysicsComponent(Entity entity, PooledEngine engine, Map map) {
        Matrix4 transform = ComponentsMapper.modelInstance.get(entity).getModelInstance().transform;
        GroundComponent groundComponent = ComponentsMapper.ground.get(entity);
        btCompoundShape compoundShape = new btCompoundShape(true);
        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        physicsComponent.init(0, compoundShape, transform);
        createGroundRegionBodyChildShapes(map, transform, groundComponent, compoundShape);
        return physicsComponent;
    }

    private void createGroundRegionBodyChildShapes(Map map, Matrix4 transform, GroundComponent groundComponent, btCompoundShape cmpShape) {
        for (int row = 0; row < SharedC.Map.REGION_SIZE_UNIT; row++)
            for (int col = 0; col < SharedC.Map.REGION_SIZE_UNIT; col++) {
                int originX = (int) transform.val[Matrix4.M03];
                int originZ = (int) transform.val[Matrix4.M23];
                GroundChildShape childShp = addChildShapeToRegionBody(map, row, col, auxMatrix);
                cmpShape.addChildShape(auxMatrix, childShp);
                groundComponent.getFrictionMapping()[row][col] = map.getPath()[originZ + row][originX + col];
            }
    }

    private GroundChildShape addChildShapeToRegionBody(Map map, int row, int col, Matrix4 localTransform) {
        Vector3 halfExtents = auxVector31.set(0.5f, 0.1f, 0.5f);
        localTransform.idt().trn(col + 0.5f, 0, row + 0.5f);
        return new GroundChildShape(halfExtents, map.getPath()[row][col]);
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
        int distanceFromOrigin = SharedC.Map.REGION_SIZE_UNIT * SharedC.Map.LEVEL_SIZE;
        createBoundaryPhysics(auxVector31.set(0, 0, 1), 0);
        createBoundaryPhysics(auxVector31.set(1, 0, 0), 0);
        createBoundaryPhysics(auxVector31.set(0, 1, 0), -1f);
        createBoundaryPhysics(auxVector31.set(0, 0, -1), -distanceFromOrigin);
        createBoundaryPhysics(auxVector31.set(-1, 0, 0), -distanceFromOrigin);
    }

    public void modelLevelGround(Map map, TextureAtlas tilesAtlas) {
        for (int row = 0; row < SharedC.Map.LEVEL_SIZE; row++) {
            for (int col = 0; col < SharedC.Map.LEVEL_SIZE; col++) {
                modelGroundRegion(map, tilesAtlas, row, col);
            }
        }
        if (!RenderSettings.SKIP_DRAWING_MODE || !RenderSettings.SKIP_DRAWING_SURROUNDING_TERRAIN) {
            modelSurroundingGround();
        }
    }

    private void modelGroundRegion(Map map, TextureAtlas tilesAtlas, int originRow, int originCol) {
        builder.begin();
        int originRowUnits = originRow * SharedC.Map.REGION_SIZE_UNIT;
        int originColUnits = originCol * SharedC.Map.REGION_SIZE_UNIT;
        for (int row = 0; row < SharedC.Map.REGION_SIZE_UNIT; row++) {
            for (int col = 0; col < SharedC.Map.REGION_SIZE_UNIT; col++) {
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
        String name = map.getUsedTiles().get(map.getPath()[row][col]).getTileName();
        if (materials.containsKey(name)) return materials.get(name);
        else {
            Material material = createGroundMaterial(tilesAtlas, name);
            materials.put(name, material);
            return material;
        }
    }

    private void modelSurroundingGround() {
        modelHorizontalSurroundingGroundModel(SharedC.Map.LEVEL_SIZE + 2,
                -1 * SharedC.Map.REGION_SIZE_UNIT * 2);
        modelHorizontalSurroundingGroundModel(SharedC.Map.LEVEL_SIZE + 2,
                SharedC.Map.LEVEL_SIZE * SharedC.Map.REGION_SIZE_UNIT);
        modelVerticalSurroundingGroundModel(-1);
        modelVerticalSurroundingGroundModel(-2);
        modelVerticalSurroundingGroundModel(1);
    }

    private void modelVerticalSurroundingGroundModel(int x) {
        for (int i = 0; i < SharedC.Map.LEVEL_SIZE; i++) {
            int regionSize = SharedC.Map.REGION_SIZE_UNIT * 2;
            modelGroundRegion(i * regionSize, x, regionSize, true);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void modelHorizontalSurroundingGroundModel(int numberOfRegions, int z) {
        for (int i = 0; i < numberOfRegions; i++) {
            int regionSize = SharedC.Map.REGION_SIZE_UNIT * 2;
            modelGroundRegion(z, i - 2, regionSize, true);
        }
    }

    private void modelGroundRegion(int z, int x, int regionSize) {
        modelGroundRegion(z, x, regionSize, false);
    }

    private void modelGroundRegion(int z, int x, int regionSize, boolean isSurrounding) {
        Entity ground = new Entity();
		TextureAtlas tiles = GameAssetManager.getInstance().get(GameC.Files.TILES_ATLAS_ASSET_NAME, TextureAtlas.class);
		boolean modelAxis = Gdx.app.getLogLevel() == Gdx.app.LOG_DEBUG && DefaultGameSettings.SHOW_AXIS && z == 0 && x == 0;
		Model region = modelGroundRegion(modelAxis, tiles, regionSize, "grass_a");
        ground.add(createGroundRegionModelInstanceComponent(z, x, region, regionSize));
        GroundComponent groundComponent = getEntitiesEngine().createComponent(GroundComponent.class);
        groundComponent.init(!isSurrounding);
        ground.add(groundComponent);
        getEntitiesEngine().addEntity(ground);
    }

    private ModelInstanceComponent createGroundRegionModelInstanceComponent(int z, int x, Model regModel, int regionSize) {
        PooledEngine engine = getEntitiesEngine();
        ModelInstanceComponent modelInstanceComponent = engine.createComponent(ModelInstanceComponent.class);
        ModelInstance modelInstance = new ModelInstance(regModel);
        x = x * regionSize;
        int y = 0;
        modelInstance.transform.setToTranslation(x, y, z);
        modelInstanceComponent.init(modelInstance);
        return modelInstanceComponent;
    }

    public void createLevelIntoEngine(Map map, ActorFactory actorFactory) {
        GameAssetManager assetManager = GameAssetManager.getInstance();
        Array<TileDefinition> usedTiles = map.getUsedTiles();
        SharedUtils.generateAtlasOfTiles(GameAssetManager.getInstance(), GameC.Files.TILES_ATLAS_ASSET_NAME, usedTiles);
        modelLevelGround(map, assetManager.get(GameC.Files.TILES_ATLAS_ASSET_NAME, TextureAtlas.class));
        createLevelPhysics(map);
        Array<PlacedActorInfo> actors = map.getActors();
        Definitions<ActorDefinition> actorsDefs = assetManager.get(SectionType.DEF + "/" + SharedC.AssetRelated.Actors.ACTORS_DEF_NAME, Definitions.class);
        PooledEngine entitiesEngine = getEntitiesEngine();
        for (PlacedActorInfo actor : actors) {
            String actorDefinitionId = actor.getActorDefinitionId();
            ActorDefinition actorDefinition = actorsDefs.getDefinitions().get(actorDefinitionId);
            Vector3 position = actor.getPosition();
            Vector3 origin = actorDefinition.getOrigin();
            String modelFileName = GameC.Files.MODELS_FOLDER_NAME + "/" + actorDefinition.getModel() + ".g3dj";
            if (actorDefinitionId.equals("tank")) {
                List<CharacterAdditionalDefinition> additionals = actorDefinition.getAdditionals();
                PlayerProperties playerProperties = Pools.obtain(PlayerProperties.class);
                playerProperties.setModelFileName(modelFileName);
                playerProperties.setX(position.x - origin.x);
                playerProperties.setY(position.y - origin.y);
                playerProperties.setZ(position.z - origin.z);
                playerProperties.setRotation(actor.getRotation());
                playerProperties.setAdditionals(additionals);
                Entity player = actorFactory.createPlayer(playerProperties);
                entitiesEngine.addEntity(player);
                entitiesEngine.getSystem(CameraSystem.class).lockToTarget(player);
                entitiesEngine.getSystem(PlayerSystem.class).setPlayer(player);
            } else {
                EnvironmentObjectDefinition environmentObjectDefinition = (EnvironmentObjectDefinition) actorDefinition;
                Entity lamp = actorFactory.createEnvironmentObject(modelFileName,
                        auxVector31.set(position.x - origin.x, position.y - origin.y, position.z - origin.z),
                        environmentObjectDefinition.isStatic(), actor.getRotation(), environmentObjectDefinition.getPointLightsDefinitions());
                entitiesEngine.addEntity(lamp);
            }

        }
    }

}
