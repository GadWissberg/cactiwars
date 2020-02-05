package com.gadarts.war.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.GameC;
import com.gadarts.war.components.CameraComponent;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.character.CharacterComponent;
import com.gadarts.war.components.character.CharacterSoundData;
import com.gadarts.war.components.character.MovementState;
import com.gadarts.war.components.physics.PhysicsComponent;
import com.gadarts.war.menu.hud.HudEventsSubscriber;
import com.gadarts.war.screens.BattleScreen;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.systems.physics.PhysicsSystem;

public class CharacterSystem extends EntitySystem implements HudEventsSubscriber {
	private static Vector3 auxVector31 = new Vector3();
	private static Vector3 auxVector32 = new Vector3();
	private static Vector2 auxVector21 = new Vector2();
	private static Vector3 rayFrom = new Vector3();
	private static Vector3 rayTo = new Vector3();
	private static Matrix4 auxMat = new Matrix4();
	private final SoundPlayer soundPlayer;

	private ImmutableArray<Entity> characters;
	private ClosestRayResultCallback callback = new ClosestRayResultCallback(rayFrom, rayTo);
	private Entity camera;

	public CharacterSystem(SoundPlayer soundPlayer) {
		this.soundPlayer = soundPlayer;
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		this.camera = getEngine().getEntitiesFor(Family.all(CameraComponent.class).get()).first();
		characters = engine.getEntitiesFor(Family.all(CharacterComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		if (!BattleScreen.isPaused()) {
			super.update(deltaTime);
			for (Entity character : characters) {
				handleMovement(character, deltaTime);
				if (DefaultGameSettings.ALLOW_SOUND && !DefaultGameSettings.MUTE_CHARACTERS_SOUNDS)
					handleCharacterSound(character);
			}
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

	private void handleMovement(Entity character, float deltaTime) {
		takeStep(character);
		handleAccelerating(character, deltaTime);
		handleRotation(character, deltaTime);
	}

	private void handleRotation(Entity character, float deltaTime) {
		CharacterComponent characterComponent = ComponentsMapper.characters.get(character);
		if (characterComponent.isRotating() && characterComponent.getSpeed() != 0 && checkIfCharacterOnGround(character)) {
			rotate(character, characterComponent, deltaTime);
		}
	}

	private void rotate(Entity character, CharacterComponent characterComponent, float deltaTime) {
		float rotation = characterComponent.getRotation() * deltaTime * GameC.CHARACTER_ROTATION_MULTIPLIER_WITH_DT;
		characterComponent.setDirection(characterComponent.getDirection(auxVector21).rotate(-rotation));
		PhysicsComponent physicsComponent = ComponentsMapper.physics.get(character);
		physicsComponent.getBody().applyTorqueImpulse(auxVector31.set(Vector3.Y).scl(rotation));
	}

	private void handleAccelerating(Entity character, float deltaTime) {
		CharacterComponent characterComponent = ComponentsMapper.characters.get(character);
		if (checkIfCharacterOnGround(character)) {
			if (accelerateAccordingToMovementState(characterComponent, deltaTime)) return;
		}
		decelerate(characterComponent);
	}

	private boolean accelerateAccordingToMovementState(CharacterComponent characterComponent, float deltaTime) {
		if (characterComponent.getMovementState() == MovementState.ACCELERATING) {
			accelerate(characterComponent, deltaTime);
			return true;
		} else if (characterComponent.getMovementState() == MovementState.REVERSE) {
			reverse(characterComponent, deltaTime);
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

	private void reverse(CharacterComponent characterComponent, float deltaTime) {
		float speed = characterComponent.getSpeed();
		float maxReverseSpeed = characterComponent.getMaxReverseSpeed();
		if (speed > -maxReverseSpeed) {
			float deltaTimeBalanced = deltaTime * GameC.CHARACTER_MOVING_MULTIPLIER_WITH_DT;
			float reverseAcc = characterComponent.getReverseAcceleration() * deltaTimeBalanced;
			float newSpeed = Math.abs(maxReverseSpeed - speed) < reverseAcc ? -maxReverseSpeed : speed - reverseAcc;
			characterComponent.setSpeed(newSpeed);
		}
	}

	private void accelerate(CharacterComponent characterComponent, float deltaTime) {
		float speed = characterComponent.getSpeed();
		float maxFrontSpeed = characterComponent.getMaxFrontSpeed();
		float deltaTimeBalanced = deltaTime * GameC.CHARACTER_MOVING_MULTIPLIER_WITH_DT;
		float acceleration = characterComponent.getAcceleration() * deltaTimeBalanced;
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
			characterComponent.getDirection(auxVector21).scl(speed);
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
}
