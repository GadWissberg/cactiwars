package com.gadarts.war.components.character;

class CharacterSpeedData {
    private SpeedData frontSpeedData = new SpeedData();
    private SpeedData reverseSpeedData = new SpeedData();
    private MovementState movementState = MovementState.IDLE;
    private float deceleration;
    private float speed;

    public float getMaxFrontSpeed() {
        return frontSpeedData.getMaxSpeed();
    }

    public void setMaxFrontSpeed(float maxFrontSpeed) {
        frontSpeedData.setMaxSpeed(maxFrontSpeed);
    }

    public float getMaxReverseSpeed() {
        return reverseSpeedData.getMaxSpeed();
    }

    public void setMaxReverseSpeed(float maxReverseSpeed) {
        reverseSpeedData.setMaxSpeed(maxReverseSpeed);
    }

    public MovementState getMovementState() {
        return movementState;
    }

    public void setMovementState(MovementState movementState) {
        this.movementState = movementState;
    }

    public float getAcceleration() {
        return frontSpeedData.getAcceleration();
    }

    public void setAcceleration(float acceleration) {
        frontSpeedData.setAcceleration(acceleration);
    }

    public float getReverseAcceleration() {
        return reverseSpeedData.getAcceleration();
    }

    public void setReverseAcceleration(float reverseAcceleration) {
        reverseSpeedData.setAcceleration(reverseAcceleration);
    }

    public float getDeceleration() {
        return deceleration;
    }

    public void setDeceleration(float deceleration) {
        this.deceleration = deceleration;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

}
