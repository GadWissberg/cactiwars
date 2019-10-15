package com.gadarts.war.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.gadarts.war.*;
import com.gadarts.war.components.*;
import com.gadarts.war.components.character.*;
import com.gadarts.war.components.physics.*;
import com.gadarts.war.sound.*;
import com.gadarts.war.systems.physics.*;

class CharacterSystem extends EntitySystem {
    private static Vector3 auxVector31 = new Vector3();
    private static Vector3 auxVector32 = new Vector3();
    private static Vector2 auxVector21 = new Vector2();
    private static Vector3 rayFrom = new Vector3();
    private static Vector3 rayTo = new Vector3();
    private static Matrix4 auxMat = new Matrix4();

    private final SoundPlayer soundPlayer;
    private ImmutableArray<Entity> characters;
    private ClosestRayResultCallback callback = new ClosestRayResultCallback(rayFrom, rayTo);

    public CharacterSystem(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        characters = engine.getEntitiesFor(Family.all(CharacterComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity character : characters) {
            handleMovement(character);
            if (GameSettings.ALLOW_SOUND) handleCharacterSound(character);
        }
    }

    private void handleCharacterSound(Entity character) {
        CharacterComponent characterComponent = ComponentsMapper.characters.get(character);
        CharacterSoundData csd = characterComponent.getCharacterSoundData();
        float speed = ComponentsMapper.physics.get(character).getBody().getLinearVelocity().len2();
        float value = MathUtils.clamp(0.5f + speed / characterComponent.getMaxFrontSpeed() * 0.5f, 0.6f, 1.6f);
        if (csd.getEnginePitch() != value) {
            csd.setEngineWorkPitch(value);
            csd.getEngineSound().setPitch(csd.getEngineSoundId(), csd.getEnginePitch());
        }
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
        float rotation = characterComponent.getRotation();
        characterComponent.setDirection(characterComponent.getDirection(auxVector21).rotate(-rotation));
        PhysicsComponent physicsComponent = ComponentsMapper.physics.get(character);
        physicsComponent.getBody().applyTorqueImpulse(auxVector31.set(0, 1, 0).scl(rotation));
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
        ComponentsMapper.physics.get(character).getBody().getMotionState().getWorldTransform(auxMat);
        auxMat.getTranslation(rayFrom);
        auxVector32.set(0, 1, 0).rot(auxMat);
        rayFrom.add(auxVector32.scl(0.5f));
        auxVector32.set(0, -1, 0).rot(auxMat);
        rayTo.set(auxVector32).scl(0.85f).add(rayFrom);
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
            float reverseAcc = characterComponent.getReverseAcceleration();
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
        if (characterComponent.getSpeed() != 0) {
            PhysicsComponent physicsComponent = ComponentsMapper.physics.get(character);
            float speed = characterComponent.getSpeed();
            characterComponent.getDirection(auxVector21).scl(speed);
            physicsComponent.getMotionState().getWorldTransform(auxMat);
            physicsComponent.getBody().setLinearVelocity(auxVector31.set(1, 0, 0).rot(auxMat).scl(speed));
        }
    }

}