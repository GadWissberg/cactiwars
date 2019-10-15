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
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gadarts.shared.LevelModeler;
import com.gadarts.shared.SharedC;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC;
import com.gadarts.war.GameSettings;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.GroundComponent;
import com.gadarts.war.components.model.ModelInstanceComponent;
import com.gadarts.war.components.physics.MotionState;
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

    private void createNorthBoundary() {
        float length = (SharedC.Level.LEVEL_SIZE * SharedC.Level.REGION_SIZE);
        Vector3 extents = auxVector31.set(length / 2f, length / 2f, 0.01f);
        Vector3 position = auxVector32.set(length / 2f, length / 2f, 0);
        createBoundaryPhysics(extents, position);
    }

    private void createSouthBoundary() {
        float length = (SharedC.Level.LEVEL_SIZE * SharedC.Level.REGION_SIZE);
        Vector3 extents = auxVector31.set(length / 2f, length / 2f, 0.01f);
        Vector3 position = auxVector32.set(length / 2f, length / 2f, length);
        createBoundaryPhysics(extents, position);
    }

    private void createWestBoundary() {
        float length = (SharedC.Level.LEVEL_SIZE * SharedC.Level.REGION_SIZE);
        Vector3 extents = auxVector31.set(0.01f, length / 2f, length / 2f);
        Vector3 position = auxVector32.set(0, length / 2f, length / 2f);
        createBoundaryPhysics(extents, position);
    }

    private void createEastBoundary() {
        float length = (SharedC.Level.LEVEL_SIZE * SharedC.Level.REGION_SIZE);
        Vector3 extents = auxVector31.set(0.01f, length / 2f, length / 2f);
        Vector3 position = auxVector32.set(length, length / 2f, length / 2f);
        createBoundaryPhysics(extents, position);
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
        engine.getSystem(PhysicsSystem.class).getCollisionWorld().addRigidBody(body);
    }

    private GroundBody createBoundaryPhysics(Vector3 boxHalfExtents, Vector3 position) {
        GroundBody boundaryBody = new GroundBody();
        MotionState ms = new MotionState();
        btRigidBody btRigidBody = createBoundaryRigidBody(boxHalfExtents, boundaryBody, ms);
        boundaryBody.setBody(btRigidBody);
        boundaryBody.setMotionState(ms);
        ms.setWorldTranslation(position);
        engine.getSystem(PhysicsSystem.class).getCollisionWorld().addRigidBody((btRigidBody) boundaryBody.getBody());
        return boundaryBody;
    }

    private btRigidBody createBoundaryRigidBody(Vector3 boxHalfExtents, GroundBody boundaryBody, MotionState ms) {
        boundaryBody.setBodyInfo(new btRigidBody.btRigidBodyConstructionInfo(0, ms, new btBoxShape(boxHalfExtents)));
        btRigidBody btRigidBody = new btRigidBody(boundaryBody.getBodyInfo());
        btRigidBody.setContactCallbackFlag(btBroadphaseProxy.CollisionFilterGroups.KinematicFilter);
        return btRigidBody;
    }


    public void createLevelPhysics() {
        createGroundBody();
        createNorthBoundary();
        createWestBoundary();
        createSouthBoundary();
        createEastBoundary();
    }

    public void modelLevelGround() {
        for (int i = 0; i < SharedC.Level.LEVEL_SIZE; i++) {
            for (int j = 0; j < SharedC.Level.LEVEL_SIZE; j++) {
                if (i == 0 && j == 0) {
                    modelTestHillGroundRegion((i % SharedC.Level.LEVEL_SIZE) * SharedC.Level.REGION_SIZE, j);
                } else {
                    modelGroundRegion((i % SharedC.Level.LEVEL_SIZE) * SharedC.Level.REGION_SIZE, j);
                }
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

    private void modelTestHillGroundRegion(int zPosition, int x) {
        Entity ground = new Entity();
        TextureAtlas tiles = GameAssetManager.getInstance().get(GameC.Files.TEXTURES_FOLDER_NAME + "/" + SharedC.TILE_FILE_NAME, TextureAtlas.class);
        Vector3 normal = new Vector3();
        ModelBuilder builder = getBuilder();
        builder.begin();
        MeshPartBuilder meshBuilder = builder.part("hill", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates, new Material(TextureAttribute.createDiffuse(tiles.findRegion("grass"))));
        meshBuilder.setUVRange(0, 0, 1, 1);

        normal.set(0.0F, 1.0F, 0.0F);

        Vector3 corner00 = new Vector3();
        Vector3 corner10 = new Vector3();
        Vector3 corner01 = new Vector3();
        Vector3 corner11 = new Vector3();
        for (int i = 0; i < SharedC.Level.REGION_SIZE - 2; ++i) {
            for (int j = 0; j < SharedC.Level.REGION_SIZE - 2; ++j) {
                int z = i % SharedC.Level.REGION_SIZE - 2;
                meshBuilder.rect(corner00.set((float) j + 1, 1, (float) z + 3), corner10.set((float) j + 1, 1, (float) (z + 1 + 3)), corner11.set((float) (j + 1 + 1), 1, (float) (z + 1 + 3)), corner01.set((float) (j + 1 + 1), 1, (float) z + 3), normal);
            }
        }
        for (int i = 0; i < SharedC.Level.REGION_SIZE - 2; ++i) {
            int z = i % SharedC.Level.REGION_SIZE - 2;
            meshBuilder.rect(corner00.set((float) 0, 0, (float) z + 3), corner10.set((float) 0, 0, (float) (z + 1 + 3)), corner11.set((float) (1), 1, (float) (z + 1 + 3)), corner01.set((float) (1), 1, (float) z + 3), normal);
        }
        meshBuilder.rect(corner00.set((float) 0, 0, SharedC.Level.REGION_SIZE - 1), corner10.set(0, 0, SharedC.Level.REGION_SIZE), corner11.set(1, 0, SharedC.Level.REGION_SIZE), corner01.set(1, 1, SharedC.Level.REGION_SIZE - 1), normal);
        for (int i = 0; i < SharedC.Level.REGION_SIZE - 2; ++i) {
            int z = SharedC.Level.REGION_SIZE - 1;
            meshBuilder.rect(corner00.set((float) i + 1, 1, (float) z), corner10.set((float) i + 1, 0, (float) z + 1), corner11.set((float) i + 2, 0, (float) z + 1), corner01.set(i + 2, 1, z), normal);
        }
        meshBuilder.rect(corner00.set(SharedC.Level.REGION_SIZE - 1, 1, SharedC.Level.REGION_SIZE - 1), corner10.set(SharedC.Level.REGION_SIZE - 1, 0, SharedC.Level.REGION_SIZE), corner11.set(SharedC.Level.REGION_SIZE, 0, SharedC.Level.REGION_SIZE), corner01.set(SharedC.Level.REGION_SIZE, 0, SharedC.Level.REGION_SIZE - 1), normal);
        for (int i = 0; i < SharedC.Level.REGION_SIZE - 2; ++i) {
            int z = i % SharedC.Level.REGION_SIZE - 2;
            meshBuilder.rect(corner00.set(SharedC.Level.REGION_SIZE - 1, 1, (float) z + 3), corner10.set(SharedC.Level.REGION_SIZE - 1, 1, (float) (z + 1 + 3)), corner11.set(SharedC.Level.REGION_SIZE, 0, (float) (z + 1 + 3)), corner01.set(SharedC.Level.REGION_SIZE, 0, (float) z + 3), normal);
        }

        Model groundModel = builder.end();


        ModelInstanceComponent modelInstanceComponent = engine.createComponent(ModelInstanceComponent.class);
        ModelInstance modelInstance = new ModelInstance(groundModel);
        x = x * SharedC.Level.REGION_SIZE;
        int y = 0;

        modelInstance.transform.setToTranslation(x, y, zPosition);
        modelInstanceComponent.init(modelInstance);
        ground.add(modelInstanceComponent);
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
