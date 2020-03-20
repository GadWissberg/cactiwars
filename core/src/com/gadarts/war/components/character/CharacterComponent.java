package com.gadarts.war.components.character;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.gadarts.shared.definitions.character.CharacterDefinition;

public class CharacterComponent implements Component, Pool.Poolable {

    private CharacterSpeedData speedData = new CharacterSpeedData();
    private float rotation;
    private float rotationDefinition;
    private float groundCrashThreshold;
    private CharacterSoundData characterSoundData = new CharacterSoundData();
    private CharacterAdditional additional;
    private CharacterDefinition characterDefinition;

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

    public void addAdditional(CharacterAdditional additional) {
        this.additional = additional;
    }

    public CharacterAdditional getAdditional() {
        return additional;
    }

    public CharacterDefinition getCharacterDefinition() {
        return characterDefinition;
    }

    public void setCharacterDefinition(CharacterDefinition characterDefinition) {
        this.characterDefinition = characterDefinition;
    }
}
