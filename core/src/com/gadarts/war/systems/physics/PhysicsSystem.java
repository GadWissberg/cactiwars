package com.gadarts.war.systems.physics;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btAxisSweep3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.physics.PhysicsComponent;
import com.gadarts.war.sound.SoundPlayer;

public class PhysicsSystem extends EntitySystem implements EntityListener, GameContactListenerEventsSubscriber {
    public static Matrix4 auxMatrix = new Matrix4();

    private final SoundPlayer soundPlayer;
    private btDiscreteDynamicsWorld collisionWorld;
    private btDefaultCollisionConfiguration collisionConfiguration;
    private btCollisionDispatcher dispatcher;
    private btAxisSweep3 broadPhase;
    private btSequentialImpulseConstraintSolver solver;
    private btGhostPairCallback ghostPairCallback;
    private ImmutableArray<Entity> physicals;
    private GameContactListener contactListener;

    public PhysicsSystem(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        collisionWorld.stepSimulation(deltaTime);
    }


    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        initializePhysics(engine);
    }

    private void initializePhysics(Engine engine) {
        Bullet.init();
        engine.addEntityListener(this);
        initializeCollisionsWorld();
        physicals = engine.getEntitiesFor(Family.all(PhysicsComponent.class).get());
        initializeContactListener();
    }

    private void initializeCollisionsWorld() {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadPhase = new btAxisSweep3(new Vector3(-100, -100, -100), new Vector3(100, 100, 100));
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadPhase, solver, collisionConfiguration);
        collisionWorld.setGravity(new Vector3(0, -20, 0));
        ghostPairCallback = new btGhostPairCallback();
        broadPhase.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
    }

    private void initializeContactListener() {
        contactListener = new GameContactListener(soundPlayer);
        contactListener.subscribe(this);
    }

    @Override
    public void entityAdded(Entity entity) {
        if (ComponentsMapper.physics.has(entity)) {
            btRigidBody body = ComponentsMapper.physics.get(entity).getBody();
            collisionWorld.addRigidBody(body);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
//        if (ComponentsMapper.bullet.has(entity)) {
//            NonCharacterPhysicsComponent nonCharacterPhysicsComponent = ComponentsMapper.bullet.get(entity);
//            collisionWorld.removeCollisionObject(nonCharacterPhysicsComponent.getBody());
//        }

    }

    public void dispose() {
        collisionWorld.dispose();
        if (solver != null) solver.dispose();
        if (broadPhase != null) broadPhase.dispose();
        if (dispatcher != null) dispatcher.dispose();
        if (collisionConfiguration != null) collisionConfiguration.dispose();
        ghostPairCallback.dispose();
        for (Entity physical : physicals) {
            ComponentsMapper.physics.get(physical).dispose();
        }
    }

    public btDiscreteDynamicsWorld getCollisionWorld() {
        return collisionWorld;
    }

    @Override
    public void onStaticEnvironmentObjectHardCollision(Entity entity) {
        PhysicsComponent envPhysicsComponent = ComponentsMapper.physics.get(entity);
        envPhysicsComponent.getBody().setAngularFactor(1);
        envPhysicsComponent.setStatic(false);
        envPhysicsComponent.recalculateLocalInertia();
        collisionWorld.addRigidBody(envPhysicsComponent.getBody());
    }
}
