package com.gadarts.war.components.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class MotionState extends btMotionState {

    private Matrix4 transform;

    public MotionState(Matrix4 transform) {
        super();
        this.transform = transform;
    }

    public MotionState() {
        this(new Matrix4());
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(transform);
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        transform.set(worldTrans);
    }

    public void setWorldTranslation(Vector3 position) {
        transform.setTranslation(position);
    }

    public Vector3 getWorldTranslation(Vector3 position) {
        transform.getTranslation(position);
        return position;
    }

    public void setTransformationObject(Matrix4 transform) {
        this.transform = transform;
    }

}
