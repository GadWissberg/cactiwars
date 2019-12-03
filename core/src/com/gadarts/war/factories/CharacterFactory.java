package com.gadarts.war.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Pools;
import com.gadarts.shared.definitions.CharacterAdditionalDefinition;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC;
import com.gadarts.war.GameC.Tank;
import com.gadarts.war.components.EnvironmentObjectComponent;
import com.gadarts.war.components.PlayerComponent;
import com.gadarts.war.components.character.CharacterAdditional;
import com.gadarts.war.components.character.CharacterComponent;
import com.gadarts.war.components.character.CharacterSoundData;
import com.gadarts.war.components.model.ModelInstanceComponent;
import com.gadarts.war.components.physics.PhysicsComponent;
import com.gadarts.war.components.physics.shapes.btBoxShapeWrapper;
import com.gadarts.war.components.physics.shapes.btCapsuleShapeXWrapper;
import com.gadarts.war.components.physics.shapes.btCylinderShapeWrapper;
import com.gadarts.war.components.physics.shapes.btSphereShapeWrapper;
import com.gadarts.war.factories.recycle.CollisionShapesPool;
import com.gadarts.war.factories.recycle.ModelInstancesPool;
import com.gadarts.war.sound.SFX;
import com.gadarts.war.sound.SoundPlayer;

import static com.gadarts.war.systems.physics.PhysicsSystem.auxMatrix;

public class CharacterFactory {
    public static Vector3 auxVector = new Vector3();
    public static Vector3 auxVector2 = new Vector3();

    private final PooledEngine engine;
    private final ModelInstancesPool modelInstancePool;
    private final CollisionShapesPool collisionShapePool;
    private final SoundPlayer soundPlayer;
    private BoundingBox auxBoundBox = new BoundingBox();

    public CharacterFactory(PooledEngine engine, SoundPlayer soundPlayer) {
        this.engine = engine;
        this.modelInstancePool = new ModelInstancesPool();
        this.collisionShapePool = new CollisionShapesPool();
        this.soundPlayer = soundPlayer;
    }

    public Entity createPlayer(String modelFileName, float x, float y, float z, float rotation, CharacterAdditionalDefinition additional) {
        Entity player = engine.createEntity();
        player.add(engine.createComponent(PlayerComponent.class));
        CharacterComponent characterComponent = createCharacterComponent();
        player.add(characterComponent);
        ModelInstanceComponent modelInstanceComponent = createModelInstanceComponent(modelFileName, x, y, z);
        player.add(modelInstanceComponent);
        ModelInstance modelInstance = modelInstanceComponent.getModelInstance();
        PhysicsComponent physicsComponent = createPhysicsComponent(modelFileName, player, modelInstance, 400);
        btRigidBody body = physicsComponent.getBody();
        body.setDamping(0, 0.1f);
        btCompoundShape collisionShape = (btCompoundShape) body.getCollisionShape();
        float halfWidth = auxBoundBox.getWidth() / 2;
        float halfHeight = auxBoundBox.getHeight() / 4;
        float halfDepth = auxBoundBox.getDepth() / 2;
        Vector3 halfExtents = auxVector.set(halfWidth, halfHeight, halfDepth);
        btBoxShape box = Pools.obtain(btBoxShapeWrapper.class);
        box.setImplicitShapeDimensions(halfExtents);
        collisionShape.addChildShape(auxMatrix.setToTranslation(0, halfExtents.y, 0), box);
        physicsComponent.recalculateLocalInertia();
        body.setContactCallbackFlag(CollisionFilterGroups.CharacterFilter);
        body.setContactCallbackFilter(CollisionFilterGroups.CharacterFilter | CollisionFilterGroups.KinematicFilter);
        player.add(physicsComponent);
//        body.getMotionState().getWorldTransform(auxMatrix);
//        body.setCenterOfMassTransform(auxMatrix.rotate(Vector3.Y,rotation));
//        body.setCollisionFlags(body.getCollisionFlags() | CF_CUSTOM_MATERIAL_CALLBACK);
        if (additional != null) {
            CharacterAdditional characterAdditional = createCharacterAdditional(additional, x, y, z, player, modelInstance);
            characterComponent.setAdditional(characterAdditional);
        }
        return player;
    }

    private CharacterAdditional createCharacterAdditional(CharacterAdditionalDefinition additionalDefinition,
                                                          float parentX,
                                                          float parentY,
                                                          float parentZ, Entity parent, ModelInstance parentModelInstance) {
        String modelFileName = GameC.Files.MODELS_FOLDER_NAME + "/" + additionalDefinition.getModel() + ".g3dj";
        Model model = GameAssetManager.getInstance().get(modelFileName, Model.class);
        ModelInstance additionalModelInstance = modelInstancePool.obtain(modelFileName, model);
        CharacterAdditional characterAdditional = Pools.obtain(CharacterAdditional.class);
        Node head = new Node();
        head.addChildren(additionalModelInstance.nodes);
        Vector3 offsetCoords = additionalDefinition.getOffsetCoords(auxVector);
        head.translation.add(offsetCoords.x, offsetCoords.y, offsetCoords.z);
        parentModelInstance.nodes.add(head);
        parentModelInstance.calculateTransforms();
        characterAdditional.init(additionalDefinition);
        return characterAdditional;
    }


    private ModelInstanceComponent createCharacterAdditionalModelInstance(CharacterAdditionalDefinition additional, float parentX, float parentY, float parentZ, String modelFileName) {
        ModelInstanceComponent miComponent = createModelInstanceComponent(modelFileName, parentX, parentY, parentZ);
        return miComponent;
    }

    private PhysicsComponent createPhysicsComponent(String modelFileName, Entity player, ModelInstance modelInstance, int mass) {
        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        btCompoundShape collisionShape = obtainBtCompoundShape(modelFileName);
        physicsComponent.init(mass, collisionShape, modelInstance.transform);
        for (int i = collisionShape.getNumChildShapes() - 1; i >= 0; i--) {
            collisionShape.removeChildShapeByIndex(i);
        }
        btRigidBody body = physicsComponent.getBody();
        body.userData = player;
        body.setSleepingThresholds(1, 1);
        body.setDeactivationTime(5);
        return physicsComponent;
    }

    private btCompoundShape obtainBtCompoundShape(String modelFileName) {
        return (btCompoundShape) collisionShapePool.obtain(modelFileName);
    }

    private ModelInstanceComponent createModelInstanceComponent(String modelFileName, float x, float y, float z) {
        Model model = GameAssetManager.getInstance().get(modelFileName, Model.class);
        ModelInstance modelInstance = modelInstancePool.obtain(modelFileName, model);
        modelInstance.transform.setToTranslation(x, y, z);
        ModelInstanceComponent modelInstanceComponent = engine.createComponent(ModelInstanceComponent.class);
        modelInstanceComponent.init(modelInstance);
        modelInstanceComponent.getBoundingBox(auxBoundBox);
        return modelInstanceComponent;
    }

    private CharacterComponent createCharacterComponent() {
        CharacterComponent characterComponent = engine.createComponent(CharacterComponent.class);
        characterComponent.init(Tank.MAX_FRONT_SPEED, Tank.ACCELERATION,
                Tank.MAX_REVERSE_SPEED, Tank.REVERSE_ACCELERATION);
        characterComponent.setGroundCrashThreshold(7.5f);
        characterComponent.setDeceleration(Tank.DECELERATION);
        characterComponent.setRotationDefinition(Tank.ROTATION);
        initializeCharacterSoundData(characterComponent);
        return characterComponent;
    }

    private void initializeCharacterSoundData(CharacterComponent characterComponent) {
        Sound engineIdleSound = GameAssetManager.getInstance().get(SFX.ENGINE.getFileName(), Sound.class);
        CharacterSoundData characterSoundData = characterComponent.getCharacterSoundData();
        characterSoundData.setEngineSound(engineIdleSound);
        characterSoundData.setEngineSoundId(soundPlayer.play(characterSoundData.getEngineSound(), true));
    }

    public Entity createEnvironmentObject(String modelFileName, Vector3 position, boolean isStatic, float rotation) {
        Entity env = engine.createEntity();
        ModelInstanceComponent modelInstanceComponent = createModelInstanceComponent(modelFileName, position.x, position.y, position.z);
        env.add(engine.createComponent(EnvironmentObjectComponent.class));
        env.add(modelInstanceComponent);
        PhysicsComponent physicsComponent = createPhysicsComponent(modelFileName, env, modelInstanceComponent.getModelInstance(), isStatic ? 0 : 10);
        physicsComponent.setStatic(true);
        btRigidBody body = physicsComponent.getBody();
        btCompoundShape collisionShape = (btCompoundShape) body.getCollisionShape();
        if (isStatic) {
            btSphereShape modelBody = Pools.obtain(btSphereShapeWrapper.class);
            collisionShape.addChildShape(auxMatrix.idt().translate(0, 0, 0), modelBody);
        } else {
            btCylinderShape modelBody = Pools.obtain(btCylinderShapeWrapper.class);
            btCapsuleShapeXWrapper head = Pools.obtain(btCapsuleShapeXWrapper.class);
            modelBody.setImplicitShapeDimensions(auxVector.set(0.1f, 2, 0.1f));
            head.setImplicitShapeDimensions(auxVector.set(0.1f, 0.2f, 0.1f));
            collisionShape.addChildShape(auxMatrix.idt().translate(0, 0, 0), modelBody);
            collisionShape.addChildShape(auxMatrix.idt().translate(0.3f, 2.2f, 0), head);

        }
        body.setAngularFactor(0);
        physicsComponent.recalculateLocalInertia();
        body.setContactCallbackFlag(CollisionFilterGroups.CharacterFilter);
        body.setContactCallbackFilter(CollisionFilterGroups.KinematicFilter);
        body.getMotionState().getWorldTransform(auxMatrix);
        body.setCenterOfMassTransform(auxMatrix.rotate(Vector3.Y, rotation));
        modelInstanceComponent.getModelInstance().transform.rotate(Vector3.Y, rotation);
        env.add(physicsComponent);
        return env;
    }
}
