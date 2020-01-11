package com.gadarts.war.screens.menu;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class CactusDec {
    private final ModelInstance modelInstance;
    private final Vector2 movingDirection = new Vector2(1, 0);
    private final BoundingBox boundingBox;
    private boolean beenInside;
    private Vector3 rotationVector = new Vector3();
    private CactusDec lastCactusCollider;

    public CactusDec(ModelInstance modelInstance, float initialSpeed) {
        this.modelInstance = modelInstance;
        boundingBox = new BoundingBox();
        movingDirection.setLength(initialSpeed);
    }

    public Vector2 getMovingVector() {
        return movingDirection;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public boolean isBeenInside() {
        return beenInside;
    }

    public void setBeenInside(boolean beenInside) {
        this.beenInside = beenInside;
    }

    public Vector3 getRotationVector() {
        return rotationVector;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public CactusDec getLastCactusCollider() {
        return lastCactusCollider;
    }

    public void setLastCactusCollider(CactusDec lastCactusCollider) {
        this.lastCactusCollider = lastCactusCollider;
    }

}
