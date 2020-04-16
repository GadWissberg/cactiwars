package com.gadarts.war.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Pool;
import com.gadarts.war.factories.ActorFactory;

public class PhysicsComponent implements Component, Pool.Poolable {
    public static Vector3 localInertia = new Vector3();
    private MotionState motionState = new MotionState();
    private btRigidBody body;
    private int mass;
    private boolean staticState;
    private btCollisionShape collisionShape;

    public void init(int mass, btCollisionShape collisionShape, Matrix4 transform) {
        this.init(mass, collisionShape, transform, false);
    }

    public void init(int mass,
                     btCollisionShape collisionShape,
                     Matrix4 transform,
                     boolean updateModelInstanceTranslationOnly) {
        this.mass = mass;
        this.collisionShape = collisionShape;
        Vector3 translation = transform.getTranslation(ActorFactory.auxVctr);
        motionState.setTransformationObject(transform.setTranslation(translation));
        motionState.setUpdateTranslationOnly(updateModelInstanceTranslationOnly);
        if (mass == 0) localInertia.setZero();
        else collisionShape.calculateLocalInertia(mass, localInertia);
        initializeBody(mass, collisionShape);
		body.setWorldTransform(transform);
    }

    public void recalculateLocalInertia() {
        if (mass == 0 || staticState) {
            localInertia.setZero();
            body.setMassProps(0, localInertia);
        } else {
            collisionShape.calculateLocalInertia(mass, localInertia);
            initializeBody(mass, collisionShape);
        }
    }

    public MotionState getMotionState() {
        return motionState;
    }

    public btRigidBody getBody() {
        return body;
    }

    @Override
    public void reset() {

    }

    private void initializeBody(float mass, btCollisionShape collisionShape) {
        if (body == null) {
            body = new btRigidBody(mass, motionState, collisionShape, localInertia);
        } else {
            redefineBodyPhysics(mass, collisionShape);
        }
    }

    private void redefineBodyPhysics(float mass, btCollisionShape collisionShape) {
        body.activate();
        body.clearForces();
        body.setCollisionShape(collisionShape);
        body.setMassProps(mass, localInertia);
        body.setLinearVelocity(Vector3.Zero);
        body.setAngularVelocity(Vector3.Zero);
    }

    public void dispose() {
        body.dispose();
    }

    public int getMass() {
        return mass;
    }

    public boolean isStatic() {
        return staticState;
    }

    public void setStatic(boolean b) {
        staticState = b;
    }
}
