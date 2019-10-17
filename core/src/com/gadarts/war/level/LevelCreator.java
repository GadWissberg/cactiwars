package com.gadarts.war.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gadarts.shared.LevelModeler;
import com.gadarts.shared.SharedC;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC;
import com.gadarts.war.GameSettings;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.GroundComponent;
import com.gadarts.war.components.model.ModelInstanceComponent;
import com.gadarts.war.components.physics.PhysicsComponent;
import com.gadarts.war.systems.physics.PhysicsSystem;

public class LevelCreator extends LevelModeler {
    private static Vector3 auxVector31 = new Vector3();
    private static Vector3 auxVector32 = new Vector3();
    private static Matrix4 auxMatrix = new Matrix4();

    private final PooledEngine engine;

    public LevelCreator(PooledEngine engine) {
        super(new ModelBuilder());
        this.engine = engine;
    }

    private void createGroundBody() {
        ImmutableArray<Entity> groundRegions = engine.getEntitiesFor(Family.all(GroundComponent.class).get());
        for (Entity entity : groundRegions) {
            createGroundRegionBody(entity);
        }
    }

    private void createGroundRegionBody(Entity entity) {
        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        ModelInstanceComponent modelInstanceComponent = ComponentsMapper.modelInstance.get(entity);
        ModelInstance modelInstance = modelInstanceComponent.getModelInstance();
        int halfWidth = SharedC.Level.REGION_SIZE;
        btCompoundShape compoundShape = new btCompoundShape(true);
        for (int i = 0; i < SharedC.Level.REGION_SIZE; i++) {
            for (int j = 0; j < SharedC.Level.REGION_SIZE; j++) {
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

    private GroundBody createBoundaryPhysics(Vector3 planeNormal, float distanceFromOrigin) {
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


    public void createLevelPhysics() {
        createGroundBody();
        int distanceFromOrigin = SharedC.Level.REGION_SIZE * SharedC.Level.LEVEL_SIZE;
        createBoundaryPhysics(auxVector31.set(0, 0, 1), 0);
        createBoundaryPhysics(auxVector31.set(1, 0, 0), 0);
        createBoundaryPhysics(auxVector31.set(0, 0, -1), -distanceFromOrigin);
        createBoundaryPhysics(auxVector31.set(-1, 0, 0), -distanceFromOrigin);
    }

    public void modelLevelGround() {
        for (int i = 0; i < SharedC.Level.LEVEL_SIZE; i++) {
            for (int j = 0; j < SharedC.Level.LEVEL_SIZE; j++) {
                modelGroundRegion((i % SharedC.Level.LEVEL_SIZE) * SharedC.Level.REGION_SIZE, j);
            }
        }
        modelSurroundingGround();
    }

    private void modelSurroundingGround() {
        modelHorizontalSurroundingGroundModel(SharedC.Level.LEVEL_SIZE + 2,
                -1 * SharedC.Level.REGION_SIZE * 2);
        modelHorizontalSurroundingGroundModel(SharedC.Level.LEVEL_SIZE + 2,
                SharedC.Level.LEVEL_SIZE * SharedC.Level.REGION_SIZE);
        modelVerticalSurroundingGroundModel(-1);
        modelVerticalSurroundingGroundModel(1);
    }

    private void modelVerticalSurroundingGroundModel(int x) {
        for (int i = 0; i < SharedC.Level.LEVEL_SIZE; i++) {
            int regionSize = SharedC.Level.REGION_SIZE * 2;
            modelGroundRegion(i * regionSize, x, regionSize);
        }
    }

    private void modelHorizontalSurroundingGroundModel(int numberOfRegions, int z) {
        for (int i = 0; i < numberOfRegions; i++) {
            int regionSize = SharedC.Level.REGION_SIZE * 2;
            modelGroundRegion(z, i - 1, regionSize);
        }
    }

    private void modelGroundRegion(int z, int x) {
        modelGroundRegion(z, x, SharedC.Level.REGION_SIZE);
    }

    private void modelGroundRegion(int z, int x, int regionSize) {
        Entity ground = new Entity();
        String grass = GameC.Files.TEXTURES_FOLDER_NAME + "/" + SharedC.TILE_FILE_NAME;
        TextureAtlas tiles = GameAssetManager.getInstance().get(grass, TextureAtlas.class);
        boolean modelAxis = Gdx.app.getLogLevel() == Gdx.app.LOG_DEBUG && GameSettings.SHOW_AXIS && z == 0 && x == 0;
        Model region = modelGroundRegion(modelAxis, tiles, regionSize);
        ground.add(createGroundRegionModelInstanceComponent(z, x, region, regionSize));
        ground.add(engine.createComponent(GroundComponent.class));
        engine.addEntity(ground);
    }

    private ModelInstanceComponent createGroundRegionModelInstanceComponent(int z, int x, Model regionModel,
                                                                            int regionSize) {
        ModelInstanceComponent modelInstanceComponent = engine.createComponent(ModelInstanceComponent.class);
        ModelInstance modelInstance = new ModelInstance(regionModel);
        x = x * regionSize;
        int y = 0;
        modelInstance.transform.setToTranslation(x, y, z);
        modelInstanceComponent.init(modelInstance);
        return modelInstanceComponent;
    }
}
