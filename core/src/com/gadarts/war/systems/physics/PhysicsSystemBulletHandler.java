package com.gadarts.war.systems.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btAxisSweep3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;

public class PhysicsSystemBulletHandler {
    private btDiscreteDynamicsWorld collisionWorld;
    private btDefaultCollisionConfiguration collisionConfiguration;
    private btCollisionDispatcher dispatcher;
    private btAxisSweep3 broadPhase;
    private btSequentialImpulseConstraintSolver solver;
    private btGhostPairCallback ghostPairCallback;

    public btDiscreteDynamicsWorld getCollisionWorld() {
        return collisionWorld;
    }

    public btDefaultCollisionConfiguration getCollisionConfiguration() {
        return collisionConfiguration;
    }

    public btCollisionDispatcher getDispatcher() {
        return dispatcher;
    }

    public btAxisSweep3 getBroadPhase() {
        return broadPhase;
    }

    public btSequentialImpulseConstraintSolver getSolver() {
        return solver;
    }

    public btGhostPairCallback getGhostPairCallback() {
        return ghostPairCallback;
    }

    public void init() {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadPhase = new btAxisSweep3(new Vector3(-100, -100, -100), new Vector3(100, 100, 100));
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadPhase, solver, collisionConfiguration);
        collisionWorld.setGravity(new Vector3(0, -20, 0));
        ghostPairCallback = new btGhostPairCallback();
        broadPhase.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
    }

    public void dispose() {
        collisionWorld.dispose();
        if (solver != null) solver.dispose();
        if (broadPhase != null) broadPhase.dispose();
        if (dispatcher != null) dispatcher.dispose();
        if (collisionConfiguration != null) collisionConfiguration.dispose();
        ghostPairCallback.dispose();
    }
}
