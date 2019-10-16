package com.gadarts.war.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Pools;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC.Artillery;
import com.gadarts.war.components.EnvironmentObjectComponent;
import com.gadarts.war.components.PlayerComponent;
import com.gadarts.war.components.character.CharacterComponent;
import com.gadarts.war.components.character.CharacterSoundData;
import com.gadarts.war.components.model.ModelInstanceComponent;
import com.gadarts.war.components.physics.PhysicsComponent;
import com.gadarts.war.components.physics.shapes.btBoxShapeWrapper;
import com.gadarts.war.components.physics.shapes.btCapsuleShapeXWrapper;
import com.gadarts.war.components.physics.shapes.btCylinderShapeWrapper;
import com.gadarts.war.factories.recycle.CollisionShapesPool;
import com.gadarts.war.factories.recycle.ModelInstancesPool;
import com.gadarts.war.sound.SFX;
import com.gadarts.war.sound.SoundPlayer;

import static com.gadarts.war.systems.physics.PhysicsSystem.auxMatrix;

public class CharacterFactory {
    public static Vector3 auxVector = new Vector3();

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

    public Entity createPlayer(String modelFileName, float x, float y, float z) {
        Entity player = engine.createEntity();
        player.add(engine.createComponent(PlayerComponent.class));
        player.add(createCharacterComponent());
        ModelInstanceComponent modelInstanceComponent = createModelInstanceComponent(modelFileName, x, z, y);
        player.add(modelInstanceComponent);
        PhysicsComponent physicsComponent = createPhysicsComponent(modelFileName, player, modelInstanceComponent.getModelInstance(), 400);
        physicsComponent.getBody().setActivationState(Collision.DISABLE_DEACTIVATION);
        physicsComponent.getBody().setAnisotropicFriction(auxVector.set(2, 0, 2));
        btCompoundShape collisionShape = (btCompoundShape) physicsComponent.getBody().getCollisionShape();
        float halfWidth = auxBoundBox.getWidth() / 2;
        float halfHeight = auxBoundBox.getHeight() / 4;
        float halfDepth = auxBoundBox.getDepth() / 2;
        Vector3 halfExtents = auxVector.set(halfWidth, halfHeight, halfDepth);
        btBoxShape box = Pools.obtain(btBoxShapeWrapper.class);
        box.setImplicitShapeDimensions(halfExtents);
        collisionShape.addChildShape(auxMatrix.translate(0, halfExtents.y, 0), box);
        physicsComponent.recalculateLocalInertia();
        player.add(physicsComponent);
        return player;
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
        body.setContactCallbackFlag(btBroadphaseProxy.CollisionFilterGroups.KinematicFilter);
        body.setContactCallbackFilter(btBroadphaseProxy.CollisionFilterGroups.KinematicFilter);
        return physicsComponent;
    }

    private btCompoundShape obtainBtCompoundShape(String modelFileName) {
        return (btCompoundShape) collisionShapePool.obtain(modelFileName);
    }

    private ModelInstanceComponent createModelInstanceComponent(String modelFileName, float x, float z, float y) {
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
        characterComponent.init(Artillery.MAX_FRONT_SPEED, Artillery.ACCELERATION,
                Artillery.MAX_REVERSE_SPEED, Artillery.REVERSE_ACCELERATION);
        characterComponent.setGroundCrashThreshold(4.5f);
        characterComponent.setDeceleration(Artillery.DECELERATION);
        characterComponent.setRotationDefinition(Artillery.ROTATION);
        initializeCharacterSoundData(characterComponent);
        return characterComponent;
    }

    private void initializeCharacterSoundData(CharacterComponent characterComponent) {
        Sound engineIdleSound = GameAssetManager.getInstance().get(SFX.ENGINE.getFileName(), Sound.class);
        CharacterSoundData characterSoundData = characterComponent.getCharacterSoundData();
        characterSoundData.setEngineSound(engineIdleSound);
        characterSoundData.setEngineSoundId(soundPlayer.play(characterSoundData.getEngineSound(), true));
    }

    public Entity createEnvironmentObject(String modelFileName, int x, int y, int z) {
        Entity env = engine.createEntity();
        ModelInstanceComponent modelInstanceComponent = createModelInstanceComponent(modelFileName, x, z, y);
        env.add(engine.createComponent(EnvironmentObjectComponent.class));
        env.add(modelInstanceComponent);
        PhysicsComponent physicsComponent = createPhysicsComponent(modelFileName, env, modelInstanceComponent.getModelInstance(), 10);
        physicsComponent.setStatic(true);
        btCompoundShape collisionShape = (btCompoundShape) physicsComponent.getBody().getCollisionShape();
        btCylinderShape body = Pools.obtain(btCylinderShapeWrapper.class);
        btCapsuleShapeXWrapper head = Pools.obtain(btCapsuleShapeXWrapper.class);
        physicsComponent.getBody().setAngularFactor(0);
        body.setImplicitShapeDimensions(auxVector.set(0.1f, 2, 0.1f));
        head.setImplicitShapeDimensions(auxVector.set(0.1f, 0.2f, 0.1f));
        collisionShape.addChildShape(auxMatrix.idt().translate(0, 0, 0), body);
        collisionShape.addChildShape(auxMatrix.idt().translate(0.3f, 2.2f, 0), head);
        physicsComponent.recalculateLocalInertia();
        env.add(physicsComponent);
        return env;
    }
}
