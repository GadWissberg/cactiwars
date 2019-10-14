package com.gadarts.war.components.character;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class CharacterComponent implements Component, Pool.Poolable {

    private CharacterSpeedData speedData = new CharacterSpeedData();
    private float rotation;
    private Vector2 direction = new Vector2(1, 0);
    private float rotationDefinition;
    private float groundCrashThreshold;
    private CharacterSoundData characterSoundData = new CharacterSoundData();

    public void init(float maxLinearVelocity, float acceleration, float maxReverseSpeed, float reverseAcceleration) {
        speedData.setMaxFrontSpeed(maxLinearVelocity);
        speedData.setAcceleration(acceleration);
        speedData.setMaxReverseSpeed(maxReverseSpeed);
        speedData.setReverseAcceleration(reverseAcceleration);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float i) {
        rotation = i;
    }

    public MovementState getMovementState() {
        return speedData.getMovementState();
    }

    public void setMovementState(MovementState movementState) {
        speedData.setMovementState(movementState);
    }

    @Override
    public void reset() {
        speedData.setDeceleration(0);
        characterSoundData.setEngineSoundId(-1);
        characterSoundData.setEngineSoundId(-1);
        characterSoundData.setEngineWorkPitch(1);
    }

    public float getRotationDefinition() {
        return rotationDefinition;
    }

    public void setRotationDefinition(float rotationDefinition) {
        this.rotationDefinition = rotationDefinition;
    }

    public float getMaxFrontSpeed() {
        return speedData.getMaxFrontSpeed();
    }

    public boolean isRotating() {
        return rotation != 0;
    }

    public Vector2 getDirection(Vector2 output) {
        return output.set(direction);
    }

    public float getSpeed() {
        return speedData.getSpeed();
    }

    public void setSpeed(float v) {
        speedData.setSpeed(v);
    }

    public float getReverseAcceleration() {
        return speedData.getReverseAcceleration();
    }

    public void setReverseAcceleration(float reverseAcceleration) {
        speedData.setReverseAcceleration(reverseAcceleration);
    }

    public float getAcceleration() {
        return speedData.getAcceleration();
    }

    public void setAcceleration(float acceleration) {
        speedData.setAcceleration(acceleration);
    }

    public float getMaxReverseSpeed() {
        return speedData.getMaxReverseSpeed();
    }

    public void setMaxReverseSpeed(float maxReverseSpeed) {
        speedData.setSpeed(maxReverseSpeed);
    }

    public void setDirection(Vector2 rotate) {
        direction.set(rotate);
    }

    public float getDeceleration() {
        return speedData.getDeceleration();
    }

    public void setDeceleration(float deceleration) {
        speedData.setDeceleration(deceleration);
    }

    public float getGroundCrashThreshold() {
        return groundCrashThreshold;
    }

    public void setGroundCrashThreshold(float groundCrashThreshold) {
        this.groundCrashThreshold = groundCrashThreshold;
    }

    public CharacterSoundData getCharacterSoundData() {
        return characterSoundData;
    }

    public void setCharacterSoundData(CharacterSoundData characterSoundData) {
        this.characterSoundData = characterSoundData;
    }
}
