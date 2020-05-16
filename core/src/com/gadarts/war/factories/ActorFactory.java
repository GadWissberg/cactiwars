package com.gadarts.war.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Pools;
import com.gadarts.shared.definitions.ActorDefinition;
import com.gadarts.shared.definitions.EnvironmentObjectDefinition;
import com.gadarts.shared.definitions.WeaponDefinition;
import com.gadarts.shared.definitions.character.CharacterAdditionalDefinition;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC;
import com.gadarts.war.GameC.Tank;
import com.gadarts.war.components.BulletComponent;
import com.gadarts.war.components.EnvironmentObjectComponent;
import com.gadarts.war.components.PlayerComponent;
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
import com.gadarts.war.factories.pools.AnimationControllerPool;
import com.gadarts.war.factories.pools.ModelInstancesPool;
import com.gadarts.war.sound.GameSound;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.sound.SoundsDefinitions;

import java.util.List;

import static com.gadarts.war.systems.physics.PhysicsSystem.*;

public class ActorFactory {
	public static Vector3 auxVector = new Vector3();
	private final PooledEngine engine;
	private final ModelInstancesPool modelInstancePool;
	private final SoundPlayer soundPlayer;
	private final BoundingBox auxBndBx = new BoundingBox();
	private final AnimationControllerPool animationControllerPool = new AnimationControllerPool();

	public ActorFactory(PooledEngine engine, SoundPlayer soundPlayer) {
		this.engine = engine;
		this.modelInstancePool = new ModelInstancesPool();
		this.soundPlayer = soundPlayer;
	}

	public Entity createPlayer(PlayerProperties playerProperties) {
		Entity player = engine.createEntity();
		createPlayerComponents(playerProperties, player);
		createPlayerAdditionals(playerProperties.getAdditionals(), player);
		//        body.setCollisionFlags(body.getCollisionFlags() | CF_CUSTOM_MATERIAL_CALLBACK);
		return player;
	}

	private void createPlayerComponents(PlayerProperties pProps, Entity player) {
		ModelInstanceComponent mic = createModelInstanceComponent(pProps.getCharacterDefinition(), pProps.getX(), pProps.getY(), pProps.getZ());
		player.add(mic);
		player.add(engine.createComponent(PlayerComponent.class));
		CharacterComponent characterComponent = createCharacterComponent();
		characterComponent.setCharacterDefinition(pProps.getCharacterDefinition());
		player.add(characterComponent);
		player.add(createPlayerPhysicsComponent(pProps.getRotation(), player, mic));
		player.add(engine.createComponent(AnimationComponent.class).init(animationControllerPool.obtain(pProps.getAnimationId(), mic.getModelInstance()), pProps.getAnimationId()));
	}

	private PhysicsComponent createPlayerPhysicsComponent(float rotation, Entity player,
														  ModelInstanceComponent modelInstanceComponent) {
		PhysicsComponent physicsComponent = createPhysicsComponent(player, modelInstanceComponent.getModelInstance(),
				800);
		createPlayerPhysicsBody(rotation, physicsComponent);
		return physicsComponent;
	}

	private void createPlayerPhysicsBody(float rotation, PhysicsComponent physicsComponent) {
		btRigidBody body = physicsComponent.getBody();
		definePlayerPhysicsBody(rotation, physicsComponent, body);
	}

	private void definePlayerPhysicsBody(float rotation, PhysicsComponent physicsComponent, btRigidBody body) {
		body.setDamping(0, 0.1f);
		Vector3 halves = auxVector.set(auxBndBx.getWidth() / 2, auxBndBx.getHeight() / 2, auxBndBx.getDepth() / 2);
		createPlayerPhysicsBodyShape(body, halves);
		physicsComponent.recalculateLocalInertia();
		defineBodyPhysicsCallbacks(body, CollisionFilterGroups.CharacterFilter | CollisionFilterGroups.KinematicFilter, CollisionFilterGroups.CharacterFilter);
		body.getMotionState().getWorldTransform(auxMatrix);
		body.setCenterOfMassTransform(auxMatrix.rotate(Vector3.Y, rotation));
	}

	private void createPlayerPhysicsBodyShape(btRigidBody body, Vector3 halfExtents) {
		btBoxShape box = Pools.obtain(btBoxShapeWrapper.class);
		box.setImplicitShapeDimensions(halfExtents);
		((btCompoundShape) body.getCollisionShape()).addChildShape(auxMatrix.setToTranslation(0, halfExtents.y, 0), box);
	}

	private void defineBodyPhysicsCallbacks(btRigidBody body, int filter, int flag) {
		body.setContactCallbackFlag(flag);
		body.setContactCallbackFilter(filter);
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
		ModelInstance additionalModelInstance = modelInstancePool.obtain(additionalDefinition.getName(), additionalDefinition.getModel());
		CharacterAdditional characterAdditional = Pools.obtain(CharacterAdditional.class);
		Node additionalNode = new Node();
		additionalNode.addChildren(additionalModelInstance.nodes);
		Vector3 offsetCoords = additionalDefinition.getOffsetCoords(auxVector);
		additionalNode.translation.add(offsetCoords.x, offsetCoords.y, offsetCoords.z);
		parentModelInstance.nodes.add(additionalNode);
		parentModelInstance.calculateTransforms();
		characterAdditional.init(additionalDefinition, additionalModelInstance);
		return characterAdditional;
	}


	private PhysicsComponent createPhysicsComponent(Entity userData, ModelInstance modelInstance, int mass) {
		PhysicsComponent physicsComponent = engine.createComponent(PhysicsComponent.class);
		btCompoundShape collisionShape = obtainBtCompoundShape();
		physicsComponent.init(mass, collisionShape, modelInstance.transform);
		for (int i = collisionShape.getNumChildShapes() - 1; i >= 0; i--) {
			collisionShape.removeChildShapeByIndex(i);
		}
		btRigidBody body = physicsComponent.getBody();
		body.userData = userData;
		body.setSleepingThresholds(1, 1);
		body.setDeactivationTime(5);
		return physicsComponent;
	}

	private btCompoundShape obtainBtCompoundShape() {
		return Pools.obtain(btCompoundShape.class);
	}

	private ModelInstanceComponent createModelInstanceComponent(ActorDefinition def, Vector3 translation) {
		return createModelInstanceComponent(def, translation.x, translation.y, translation.z);
	}

	private ModelInstanceComponent createModelInstanceComponent(ActorDefinition def, float x, float y, float z) {
		ModelInstance modelInstance = modelInstancePool.obtain(def.getName(), def.getModel());
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
		characterComponent.setGroundCrashThreshold(15);
		characterComponent.setDeceleration(Tank.DECELERATION);
		characterComponent.setRotationDefinition(Tank.ROTATION);
		initializeCharacterSoundData(characterComponent);
		return characterComponent;
	}

	private void initializeCharacterSoundData(CharacterComponent characterComponent) {
		GameSound engineIdleSound = GameAssetManager.getInstance().get(SoundsDefinitions.ENGINE.getFileName(), GameSound.class);
		CharacterSoundData characterSoundData = characterComponent.getCharacterSoundData();
		characterSoundData.setEngineSound(engineIdleSound);
		characterSoundData.setEngineSoundId(soundPlayer.play(characterSoundData.getEngineSound(), true));
	}

	public Entity createEnvironmentObject(EnvironmentObjectDefinition environmentObjectDefinition, Vector3 position, boolean isStatic, float rotation) {
		Entity env = engine.createEntity();
		ModelInstanceComponent modelInstanceComponent = createModelInstanceComponent(environmentObjectDefinition, position.x, position.y, position.z);
		env.add(engine.createComponent(EnvironmentObjectComponent.class));
		env.add(modelInstanceComponent);
		ModelInstance modelInstance = modelInstanceComponent.getModelInstance();
		PhysicsComponent physicsComponent = createPhysicsComponent(env, modelInstance, isStatic ? 0 : 100);
		physicsComponent.setStatic(true);
		btRigidBody body = physicsComponent.getBody();
		btCompoundShape collisionShape = (btCompoundShape) body.getCollisionShape();
		if (isStatic) {
			btCollisionShape modelBody;
			if (environmentObjectDefinition.getName().equals("building_1") || environmentObjectDefinition.getName().equals("building_2")) {
				modelBody = new btBoxShape(new Vector3(1, 4, 3));
			} else if (environmentObjectDefinition.getName().equals("building_3") || environmentObjectDefinition.getName().equals("building_4")) {
				modelBody = new btBoxShape(new Vector3(2, 4, 3));
			} else if (environmentObjectDefinition.getName().equals("rock_1")) {
				modelBody = Pools.obtain(btSphereShapeWrapper.class);
			} else {
				modelBody = new btSphereShape(0.4f);
			}
			collisionShape.addChildShape(auxMatrix.setToTranslation(0, 0, 0), modelBody);
		} else {
			btCylinderShape modelBody = Pools.obtain(btCylinderShapeWrapper.class);
			btCapsuleShapeXWrapper head = Pools.obtain(btCapsuleShapeXWrapper.class);
			modelBody.setImplicitShapeDimensions(auxVector.set(0.1f, 2, 0.1f));
			head.setImplicitShapeDimensions(auxVector.set(0.2f, 0.1f, 0.1f));
			collisionShape.addChildShape(auxMatrix.setToTranslation(0.3f, 2.2f, 0), head);
			collisionShape.addChildShape(auxMatrix.setToTranslation(0, 0, 0), modelBody);
		}
		body.setAngularFactor(0);
		physicsComponent.recalculateLocalInertia();
		defineBodyPhysicsCallbacks(body, CollisionFilterGroups.KinematicFilter, CollisionFilterGroups.KinematicFilter);
		body.getMotionState().getWorldTransform(auxMatrix);
		body.setCenterOfMassTransform(auxMatrix.rotate(Vector3.Y, rotation));
		modelInstance.transform.rotate(Vector3.Y, rotation);
		String texture = environmentObjectDefinition.getTexture();
		if (texture != null) {
			for (Material material : modelInstance.materials) {
				if (material.has(TextureAttribute.Diffuse)) {
					TextureAttribute attribute = (TextureAttribute) material.get(TextureAttribute.Diffuse);
					attribute.textureDescription.texture = GameAssetManager.getInstance().get(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + texture + ".png", Files.FileType.Internal).path(), Texture.class);
				}
			}
		}
		env.add(physicsComponent);
		return env;
	}

	public void createBullet(Matrix4 rotation, Vector3 worldTrans, WeaponDefinition weapon) {
		Entity entity = engine.createEntity();
		BulletComponent bulletComponent = engine.createComponent(BulletComponent.class);
		entity.add(bulletComponent);
		Vector3 forwardVct = auxVector3_2.set(1f, 0, 0).rot(rotation);
		ModelInstanceComponent modelComponent = addBulletModelInstanceComponent(worldTrans, weapon, forwardVct, entity);
		entity.add(createBulletPhysics(entity, modelComponent, forwardVct));
		engine.addEntity(entity);
	}

	private ModelInstanceComponent addBulletModelInstanceComponent(Vector3 worldTranslation,
																   WeaponDefinition weapon,
																   Vector3 forwardVector,
																   Entity bulletEntity) {
		worldTranslation.add(forwardVector.x, forwardVector.y, forwardVector.z);
		ModelInstanceComponent modelComponent = createModelInstanceComponent(weapon, worldTranslation);
		modelComponent.getModelInstance().transform.rotate(Vector3.X, forwardVector);
		bulletEntity.add(modelComponent);
		return modelComponent;
	}

	private PhysicsComponent createBulletPhysics(Entity bulletEntity,
												 ModelInstanceComponent modelCmp,
												 Vector3 direction) {
		PhysicsComponent physicsComponent = createPhysicsComponent(bulletEntity, modelCmp.getModelInstance(), 1);
		physicsComponent.getMotionState().setUpdateTranslationOnly(true);
		btRigidBody body = defineBulletPhysicsBody(physicsComponent);
		defineBulletShape(body);
		Vector3 impulse = auxVector3_1.set(direction.x, direction.y, direction.z);
		physicsComponent.getBody().applyCentralImpulse(impulse.setLength2(800));
		return physicsComponent;
	}

	private btRigidBody defineBulletPhysicsBody(PhysicsComponent physicsComponent) {
		btRigidBody body = physicsComponent.getBody();
		defineBodyPhysicsCallbacks(body, CollisionFilterGroups.CharacterFilter | CollisionFilterGroups.KinematicFilter, CollisionFilterGroups.CharacterFilter);
		body.setGravity(auxVector3_1.setZero());
		body.activate();
		body.setDamping(0, 0);
		return body;
	}

	private void defineBulletShape(btRigidBody body) {
		btSphereShape shape = Pools.obtain(btSphereShapeWrapper.class);
		shape.setImplicitShapeDimensions(auxVector.set(0.1f, 0.1f, 0.1f));
		btCompoundShape collisionShape = (btCompoundShape) body.getCollisionShape();
		collisionShape.addChildShape(auxMatrix.idt().translate(0, 0, 0), shape);
	}
}
