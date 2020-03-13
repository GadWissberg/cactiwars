package com.gadarts.war.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Pools;
import com.gadarts.shared.definitions.Definitions;
import com.gadarts.shared.definitions.PointLightDefinition;
import com.gadarts.shared.definitions.character.CharacterAdditionalDefinition;
import com.gadarts.shared.par.SectionType;
import com.gadarts.shared.par.inflations.DefinitionType;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC;
import com.gadarts.war.GameC.Tank;
import com.gadarts.war.components.*;
import com.gadarts.war.components.character.CharacterAdditional;
import com.gadarts.war.components.character.CharacterComponent;
import com.gadarts.war.components.character.CharacterSoundData;
import com.gadarts.war.components.model.AnimationComponent;
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

import java.util.List;
import java.util.Map;

import static com.gadarts.war.systems.physics.PhysicsSystem.auxMatrix;

public class ActorFactory {
    public static Vector3 auxVctr = new Vector3();

    private final PooledEngine engine;
    private final ModelInstancesPool modelInstancePool;
    private final CollisionShapesPool collisionShapePool;
    private final SoundPlayer soundPlayer;
    private final Map weaponsDefinitions;
    private BoundingBox auxBndBx = new BoundingBox();

    public ActorFactory(PooledEngine engine, SoundPlayer soundPlayer) {
        this.engine = engine;
        this.modelInstancePool = new ModelInstancesPool();
        this.collisionShapePool = new CollisionShapesPool();
        this.soundPlayer = soundPlayer;
        GameAssetManager instance = GameAssetManager.getInstance();
        weaponsDefinitions = instance.getGameAsset(SectionType.DEF, DefinitionType.TILES.name(), Definitions.class).getDefinitions();
    }

    public Entity createPlayer(PlayerProperties playerProperties) {
        Entity player = engine.createEntity();
        createPlayerComponents(playerProperties, player);
        createPlayerAdditionals(playerProperties.getAdditionals(), player);
        //        body.setCollisionFlags(body.getCollisionFlags() | CF_CUSTOM_MATERIAL_CALLBACK);
        return player;
    }

    private void createPlayerComponents(PlayerProperties pProps, Entity player) {
        String model = pProps.getModelFileName();
        ModelInstanceComponent mic = createModelInstanceComponent(model, pProps.getX(), pProps.getY(), pProps.getZ());
        player.add(mic);
        player.add(engine.createComponent(PlayerComponent.class));
        CharacterComponent characterComponent = createCharacterComponent();
        characterComponent.setCharacterDefinition(pProps.getCharacterDefinition());
        player.add(characterComponent);
        player.add(createPlayerPhysicsComponent(model, pProps.getRotation(), player, mic));
        player.add(engine.createComponent(AnimationComponent.class).init(mic.getModelInstance()));
    }

    private PhysicsComponent createPlayerPhysicsComponent(String modelFileName, float rotation, Entity player,
                                                          ModelInstanceComponent modelInstanceComponent) {
        PhysicsComponent physicsComponent = createPhysicsComponent(
                modelFileName,
                player,
                modelInstanceComponent.getModelInstance(),
                400);
        createPlayerPhysicsBody(rotation, physicsComponent);
        return physicsComponent;
    }

    private void createPlayerPhysicsBody(float rotation, PhysicsComponent physicsComponent) {
        btRigidBody body = physicsComponent.getBody();
        definePlayerPhysicsBody(rotation, physicsComponent, body);
    }

    private void definePlayerPhysicsBody(float rotation, PhysicsComponent physicsComponent, btRigidBody body) {
        body.setDamping(0, 0.1f);
        Vector3 halves = auxVctr.set(auxBndBx.getWidth() / 2, auxBndBx.getHeight() / 2, auxBndBx.getDepth() / 2);
        createPlayerPhysicsBodyShape(body, halves);
        physicsComponent.recalculateLocalInertia();
        definePlyrPhysicsCallbacks(body, CollisionFilterGroups.CharacterFilter | CollisionFilterGroups.KinematicFilter);
        body.getMotionState().getWorldTransform(auxMatrix);
        body.setCenterOfMassTransform(auxMatrix.rotate(Vector3.Y, rotation));
    }

    private void createPlayerPhysicsBodyShape(btRigidBody body, Vector3 halfExtents) {
        btBoxShape box = Pools.obtain(btBoxShapeWrapper.class);
        box.setImplicitShapeDimensions(halfExtents);
        ((btCompoundShape) body.getCollisionShape()).addChildShape(auxMatrix.setToTranslation(0, halfExtents.y, 0), box);
    }

    private void definePlyrPhysicsCallbacks(btRigidBody body, int i) {
        body.setContactCallbackFlag(CollisionFilterGroups.CharacterFilter);
        body.setContactCallbackFilter(i);
    }

    private void createPlayerAdditionals(List<CharacterAdditionalDefinition> additionals, Entity player) {
        ModelInstanceComponent modelInstanceComponent = player.getComponent(ModelInstanceComponent.class);
        CharacterComponent characterComponent = player.getComponent(CharacterComponent.class);
        if (additionals != null)
            for (CharacterAdditionalDefinition definition : additionals) {
                ModelInstance modelInstance = modelInstanceComponent.getModelInstance();
                CharacterAdditional characterAdditional = createCharacterAdditional(definition, modelInstance);
                characterComponent.addAdditional(characterAdditional);
            }
    }

    private CharacterAdditional createCharacterAdditional(CharacterAdditionalDefinition additionalDefinition,
                                                          ModelInstance parentModelInstance) {
        String modelFileName = GameC.Files.MODELS_FOLDER_NAME + "/" + additionalDefinition.getModel() + ".g3dj";
        Model model = GameAssetManager.getInstance().get(modelFileName, Model.class);
        ModelInstance additionalModelInstance = modelInstancePool.obtain(modelFileName, model);
        CharacterAdditional characterAdditional = Pools.obtain(CharacterAdditional.class);
        Node additionalNode = new Node();
        additionalNode.addChildren(additionalModelInstance.nodes);
        Vector3 offsetCoords = additionalDefinition.getOffsetCoords(auxVctr);
        additionalNode.translation.add(offsetCoords.x, offsetCoords.y, offsetCoords.z);
        parentModelInstance.nodes.add(additionalNode);
        parentModelInstance.calculateTransforms();
        characterAdditional.init(additionalDefinition);
        return characterAdditional;
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
        modelInstanceComponent.getBoundingBox(auxBndBx);
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
        if (!DefaultGameSettings.ALLOW_SOUND || DefaultGameSettings.MUTE_CHARACTERS_SOUNDS) return;
        Sound engineIdleSound = GameAssetManager.getInstance().get(SFX.ENGINE.getFileName(), Sound.class);
        CharacterSoundData characterSoundData = characterComponent.getCharacterSoundData();
        characterSoundData.setEngineSound(engineIdleSound);
        characterSoundData.setEngineSoundId(soundPlayer.play(characterSoundData.getEngineSound(), true));
    }

    public Entity createEnvironmentObject(String modelFileName, Vector3 position, boolean isStatic, float rotation, List<PointLightDefinition> pointLightsDefinitions) {
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
            modelBody.setImplicitShapeDimensions(auxVctr.set(0.1f, 2, 0.1f));
            head.setImplicitShapeDimensions(auxVctr.set(0.1f, 0.2f, 0.1f));
            collisionShape.addChildShape(auxMatrix.idt().translate(0, 0, 0), modelBody);
            collisionShape.addChildShape(auxMatrix.idt().translate(0.3f, 2.2f, 0), head);

        }
        body.setAngularFactor(0);
        physicsComponent.recalculateLocalInertia();
        definePlyrPhysicsCallbacks(body, CollisionFilterGroups.KinematicFilter);
        body.getMotionState().getWorldTransform(auxMatrix);
        body.setCenterOfMassTransform(auxMatrix.rotate(Vector3.Y, rotation));
        modelInstanceComponent.getModelInstance().transform.rotate(Vector3.Y, rotation);
        env.add(physicsComponent);
        if (pointLightsDefinitions != null && pointLightsDefinitions.size() > 0) {
            for (PointLightDefinition pointLightDefinition : pointLightsDefinitions) {
                Entity pointLight = engine.createEntity();
                PointLightComponent component = engine.createComponent(PointLightComponent.class);
                pointLight.add(component);
                component.init(pointLightDefinition, env);
                engine.addEntity(pointLight);
                ComponentsMapper.environmentObject.get(env).addSourceLight(pointLight);
            }
        }
        return env;
    }

    public void createBullet(Vector2 direction, Vector3 worldTranslation, String weapon) {
        if (!weaponsDefinitions.containsKey(weapon)) return;
        Object weaponDefinition = weaponsDefinitions.get(weapon);
        Entity bulletEntity = engine.createEntity();
        BulletComponent bulletComponent = engine.createComponent(BulletComponent.class);
        PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
        bulletEntity.add(bulletComponent);
        bulletEntity.add(physicsComponent);
        physicsComponent.getBody().getMotionState().setWorldTransform(auxMatrix.setTranslation(worldTranslation));
        engine.addEntity(bulletEntity);
    }
}
