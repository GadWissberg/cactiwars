package com.gadarts.war.components.character;

class CharacterSpeedData {
    private float maxFrontSpeed;
    private float maxReverseSpeed;
    private MovementState movementState = MovementState.IDLE;
    private float acceleration;
    private float reverseAcceleration;
    private float deceleration;
    private float speed;

    public void setMaxFrontSpeed(float maxFrontSpeed) {
        this.maxFrontSpeed = maxFrontSpeed;
    }

    public void setMaxReverseSpeed(float maxReverseSpeed) {
        this.maxReverseSpeed = maxReverseSpeed;
    }

    public void setMovementState(MovementState movementState) {
        this.movementState = movementState;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public void setReverseAcceleration(float reverseAcceleration) {
        this.reverseAcceleration = reverseAcceleration;
    }

    public void setDeceleration(float deceleration) {
        this.deceleration = deceleration;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getMaxFrontSpeed() {
        return maxFrontSpeed;
    }

    public float getMaxReverseSpeed() {
        return maxReverseSpeed;
    }

    public MovementState getMovementState() {
        return movementState;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getReverseAcceleration() {
        return reverseAcceleration;
    }

    public float getDeceleration() {
        return deceleration;
    }

    public float getSpeed() {
        return speed;
    }

}
