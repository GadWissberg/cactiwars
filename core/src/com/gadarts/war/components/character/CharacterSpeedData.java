package com.gadarts.war.components.character;

class CharacterSpeedData {
    private SpeedData frontSpeedData = new SpeedData();
    private SpeedData reverseSpeedData = new SpeedData();
    private MovementState movementState = MovementState.IDLE;
    private float deceleration;
    private float speed;

    public void setMaxFrontSpeed(float maxFrontSpeed) {
        frontSpeedData.setMaxSpeed(maxFrontSpeed);
    }

    public void setMaxReverseSpeed(float maxReverseSpeed) {
        reverseSpeedData.setMaxSpeed(maxReverseSpeed);
    }

    public void setMovementState(MovementState movementState) {
        this.movementState = movementState;
    }

    public void setAcceleration(float acceleration) {
        frontSpeedData.setAcceleration(acceleration);
    }

    public void setReverseAcceleration(float reverseAcceleration) {
        reverseSpeedData.setAcceleration(reverseAcceleration);
    }

    public void setDeceleration(float deceleration) {
        this.deceleration = deceleration;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getMaxFrontSpeed() {
        return frontSpeedData.getMaxSpeed();
    }

    public float getMaxReverseSpeed() {
        return reverseSpeedData.getMaxSpeed();
    }

    public MovementState getMovementState() {
        return movementState;
    }

    public float getAcceleration() {
        return frontSpeedData.getAcceleration();
    }

    public float getReverseAcceleration() {
        return reverseSpeedData.getAcceleration();
    }

    public float getDeceleration() {
        return deceleration;
    }

    public float getSpeed() {
        return speed;
    }

}
