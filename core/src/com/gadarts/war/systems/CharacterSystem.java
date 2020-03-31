package com.gadarts.war.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Timer;
import com.gadarts.shared.definitions.WeaponDefinition;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.GameC;
import com.gadarts.war.components.CameraComponent;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.character.CharacterAdditional;
import com.gadarts.war.components.character.CharacterComponent;
import com.gadarts.war.components.character.CharacterSoundData;
import com.gadarts.war.components.character.MovementState;
import com.gadarts.war.components.physics.MotionState;
import com.gadarts.war.components.physics.PhysicsComponent;
import com.gadarts.war.factories.ActorFactory;
import com.gadarts.war.menu.hud.HudEventsSubscriber;
import com.gadarts.war.screens.BattleScreen;
import com.gadarts.war.sound.SFX;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.systems.physics.GameContactListenerEventsSubscriber;
import com.gadarts.war.systems.physics.PhysicsSystem;

import java.util.List;

import static com.gadarts.war.systems.physics.PhysicsSystem.auxMatrix;
import static com.gadarts.war.systems.physics.PhysicsSystem.auxVector3_1;

public class CharacterSystem extends GameEntitySystem implements HudEventsSubscriber, GameContactListenerEventsSubscriber {
	private final static Vector3 auxVector31 = new Vector3();
	private final static Vector3 auxVector32 = new Vector3();
	private final static Vector3 rayFrom = new Vector3();
	private final static Vector3 rayTo = new Vector3();
	private final static Matrix4 auxMat = new Matrix4();

	private final SoundPlayer soundPlayer;
	private final ActorFactory actorFactory;

	private ImmutableArray<Entity> characters;
	private ClosestRayResultCallback callback = new ClosestRayResultCallback(rayFrom, rayTo);
	private Entity camera;
	private Timer timer = new Timer();

	public CharacterSystem(SoundPlayer soundPlayer, ActorFactory actorFactory) {
		this.soundPlayer = soundPlayer;
		this.actorFactory = actorFactory;
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		this.camera = getEngine().getEntitiesFor(Family.all(CameraComponent.class).get()).first();
		characters = engine.getEntitiesFor(Family.all(CharacterComponent.class).get());
		engine.getSystem(PhysicsSystem.class).subscribeForCollisionEvents(this);
	}

	@Override
	public void update(float deltaTime) {
		if (!BattleScreen.isPaused()) {
			super.update(deltaTime);
			for (Entity character : characters) {
				updateCharacter(character, deltaTime);
			}
		}
	}

	private void updateCharacter(Entity character, float deltaTime) {
		handleMovement(character);
		if (DefaultGameSettings.ALLOW_SOUND && !DefaultGameSettings.MUTE_CHARACTERS_SOUNDS)
			handleCharacterSound(character);
		handleShooting(character);
		List<CharacterAdditional> additionals = ComponentsMapper.characters.get(character).getAdditionals();
		if (additionals != null) for (CharacterAdditional additional : additionals) {
			additional.update(deltaTime);
		}
	}

	private void handleShooting(Entity character) {
		CharacterComponent characterComponent = ComponentsMapper.characters.get(character);
		if (characterComponent.isShooting()) {
			if (!characterComponent.isReloading()) {
				shoot(character, characterComponent);
			}
		}
	}

	private void shoot(Entity character, CharacterComponent characterComponent) {
		MotionState motionState = ComponentsMapper.physics.get(character).getMotionState();
		Vector3 worldTranslation = motionState.getWorldTranslation(auxVector3_1);
		WeaponDefinition weapon = characterComponent.getCharacterDefinition().getWeapon();
		motionState.getWorldTransform(PhysicsSystem.auxMatrix);
		actorFactory.createBullet(auxMatrix, worldTranslation.add(characterComponent.getCharacterDefinition().getBulletPosition(auxVector31)), weapon);
		characterComponent.setReloading(true);
		timer.scheduleTask(characterComponent.getFinishReloadTask(), 1);
		List<CharacterAdditional> additionals = ComponentsMapper.characters.get(character).getAdditionals();
		if (additionals != null) for (CharacterAdditional additional : additionals) {
			additional.onShoot();
		}
		if (!DefaultGameSettings.MUTE_CHARACTERS_SOUNDS) {
			soundPlayer.play(MathUtils.randomBoolean() ? SFX.CANNON_SHOOT_1 : SFX.CANNON_SHOOT_2, ComponentsMapper.camera.get(camera).getCamera(), ComponentsMapper.physics.get(character).getMotionState().getWorldTranslation(auxVector31));
		}
	}

	private void handleCharacterSound(Entity character) {
		handleCharacterSoundPitch(character);
		handleCharacterSoundVolumeAndPan(character);
	}

	private void handleCharacterSoundPitch(Entity character) {
		CharacterComponent characterComponent = ComponentsMapper.characters.get(character);
		CharacterSoundData csd = characterComponent.getCharacterSoundData();
		float speed = ComponentsMapper.physics.get(character).getBody().getLinearVelocity().len2();
		float value = MathUtils.clamp(0.5f + speed / characterComponent.getMaxFrontSpeed() * 0.5f, 0.6f, 1.6f);
		if (csd.getEnginePitch() != value) {
			csd.setEngineWorkPitch(value);
			csd.getEngineSound().setPitch(csd.getEngineSoundId(), csd.getEnginePitch());
		}
	}

	private void handleCharacterSoundVolumeAndPan(Entity character) {
		CharacterComponent characterComponent = ComponentsMapper.characters.get(character);
		CharacterSoundData csd = characterComponent.getCharacterSoundData();
		ComponentsMapper.physics.get(character).getMotionState().getWorldTranslation(auxVector31);
		PerspectiveCamera camera = ComponentsMapper.camera.get(this.camera).getCamera();
		float pan = SoundPlayer.calculatePan(camera, auxVector31);
		float dst = auxVector31.dst2(camera.position);
		float volume = MathUtils.norm(0, 2, 10000f / (dst * dst));
		csd.getEngineSound().setPan(csd.getEngineSoundId(), pan, volume);
	}

	private void handleMovement(Entity character) {
		takeStep(character);
		handleAccelerating(character);
		handleRotation(character);
	}

	private void handleRotation(Entity character) {
		CharacterComponent characterComponent = ComponentsMapper.characters.get(character);
		if (characterComponent.isRotating() && characterComponent.getSpeed() != 0 && checkIfCharacterOnGround(character)) {
			rotate(character, characterComponent);
		}
	}

	private void rotate(Entity character, CharacterComponent characterComponent) {
		float rotation = characterComponent.getRotation() * GameC.CHARACTER_ROTATION_MULTIPLIER_WITH_DT;
		PhysicsComponent physicsComponent = ComponentsMapper.physics.get(character);
		physicsComponent.getBody().applyTorqueImpulse(auxVector31.set(Vector3.Y).scl(rotation));
	}

	private void handleAccelerating(Entity character) {
		CharacterComponent characterComponent = ComponentsMapper.characters.get(character);
		if (checkIfCharacterOnGround(character)) {
			if (accelerateAccordingToMovementState(characterComponent)) return;
		}
		decelerate(characterComponent);
	}

	private boolean accelerateAccordingToMovementState(CharacterComponent characterComponent) {
		if (characterComponent.getMovementState() == MovementState.ACCELERATING) {
			accelerate(characterComponent);
			return true;
		} else if (characterComponent.getMovementState() == MovementState.REVERSE) {
			reverse(characterComponent);
			return true;
		}
		return false;
	}

	private boolean checkIfCharacterOnGround(Entity character) {
		initializeRayForTest(character);
		getEngine().getSystem(PhysicsSystem.class).getCollisionWorld().rayTest(rayFrom, rayTo, callback);
		return callback.hasHit();
	}

	private void initializeRayForTest(Entity character) {
		Matrix4 characterTransform = auxMat;
		ComponentsMapper.physics.get(character).getBody().getMotionState().getWorldTransform(characterTransform);
		characterTransform.getTranslation(rayFrom);
		auxVector32.set(0, -1, 0).rot(characterTransform);
		rayTo.set(auxVector32).scl(0.1f).add(rayFrom);
		callback.setCollisionObject(null);
		callback.setClosestHitFraction(1);
	}

	private void decelerate(CharacterComponent characterComponent) {
		float speed = characterComponent.getSpeed();
		float acceleration = characterComponent.getDeceleration();
		if (speed > 0) {
			characterComponent.setSpeed(speed - acceleration < 0 ? 0 : speed - acceleration);
		} else if (speed < 0) {
			characterComponent.setSpeed(speed + acceleration > 0 ? 0 : speed + acceleration);
		}
	}

	private void reverse(CharacterComponent characterComponent) {
		float speed = characterComponent.getSpeed();
		float maxReverseSpeed = characterComponent.getMaxReverseSpeed();
		if (speed > -maxReverseSpeed) {
			float deltaTimeBalanced = GameC.CHARACTER_MOVING_MULTIPLIER_WITH_DT;
			float reverseAcc = characterComponent.getReverseAcceleration() * deltaTimeBalanced;
			float newSpeed = Math.abs(maxReverseSpeed - speed) < reverseAcc ? -maxReverseSpeed : speed - reverseAcc;
			characterComponent.setSpeed(newSpeed);
		}
	}

	private void accelerate(CharacterComponent characterComponent) {
		float speed = characterComponent.getSpeed();
		float maxFrontSpeed = characterComponent.getMaxFrontSpeed();
		float acceleration = characterComponent.getAcceleration();
		if (speed < maxFrontSpeed) {
			float newSpeed = Math.abs(maxFrontSpeed - speed) < acceleration ? maxFrontSpeed : speed + acceleration;
			characterComponent.setSpeed(newSpeed);
		}
	}

	private void takeStep(Entity character) {
		CharacterComponent characterComponent = ComponentsMapper.characters.get(character);
		float speed = characterComponent.getSpeed();
		if (speed != 0) {
			btRigidBody body = ComponentsMapper.physics.get(character).getBody();
			ComponentsMapper.physics.get(character).getMotionState().getWorldTransform(auxMat);
			body.setLinearVelocity(auxVector31.set(Vector3.X).rot(auxMat).scl(speed));
			if (!body.isActive()) body.activate();
		}
	}

	@Override
	public void onMenuActivated() {
		if (!DefaultGameSettings.ALLOW_SOUND) return;
		for (Entity character : characters) {
			CharacterSoundData characterSoundData = ComponentsMapper.characters.get(character).getCharacterSoundData();
			soundPlayer.pauseSound(characterSoundData.getEngineSound(), characterSoundData.getEngineSoundId());
		}
	}

	@Override
	public void onMenuDeactivated() {
		if (!DefaultGameSettings.ALLOW_SOUND) return;
		for (Entity character : characters) {
			CharacterSoundData characterSoundData = ComponentsMapper.characters.get(character).getCharacterSoundData();
			soundPlayer.resumeSound(characterSoundData.getEngineSound(), characterSoundData.getEngineSoundId());
		}
	}

	@Override
	public void onResize(int width, int height) {

	}

	@Override
	public void onStaticEnvironmentObjectHardCollision(Entity entity) {

	}

	@Override
	public void onBulletWithEnvironmentObject(Entity bullet, Entity env) {
		getEngine().removeEntity(bullet);
	}

	@Override
	public void onBulletWithGround(Entity bullet, Entity ground) {
		getEngine().removeEntity(bullet);
	}
}
