package com.gadarts.war.systems.physics;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gadarts.war.BattleScreen;
import com.gadarts.war.GameSettings;
import com.gadarts.war.components.CameraComponent;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.physics.PhysicsComponent;
import com.gadarts.war.sound.SoundPlayer;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem extends EntitySystem implements EntityListener, GameContactListenerEventsSubscriber {
    public static Matrix4 auxMatrix = new Matrix4();
    public static Vector3 auxVector = new Vector3();
    public static Vector3 auxVector2 = new Vector3();

    private final SoundPlayer soundPlayer;
    private PhysicsSystemBulletHandler bulletHandler = new PhysicsSystemBulletHandler();
    private ImmutableArray<Entity> physicals;
    private GameContactListener contactListener;
    private List<PhysicsSystemEventsSubscriber> subscribers = new ArrayList<PhysicsSystemEventsSubscriber>();

    public PhysicsSystem(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void update(float deltaTime) {
        if (!BattleScreen.isPaused()) {
            super.update(deltaTime);
            btDiscreteDynamicsWorld collisionWorld = bulletHandler.getCollisionWorld();
            collisionWorld.stepSimulation(deltaTime, 5);
        }
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
        initializeCollisionShapesDrawing();
    }

    private void initializeCollisionShapesDrawing() {
        if (GameSettings.DRAW_COLLISION_SHAPES) {
            bulletHandler.initializeDebugDrawer();
            for (PhysicsSystemEventsSubscriber sub : subscribers) {
                sub.collisionShapesDrawingInitialized(bulletHandler.getCollisionShapesDebugDrawingMethod());
            }
        }
    }

    private void initializeCollisionsWorld() {
        bulletHandler.init();
    }

    private void initializeContactListener() {
        Entity camera = getEngine().getEntitiesFor(Family.all(CameraComponent.class).get()).first();
        contactListener = new GameContactListener(soundPlayer, camera);
        contactListener.subscribe(this);
    }

    @Override
    public void entityAdded(Entity entity) {
        if (ComponentsMapper.physics.has(entity)) {
            btRigidBody body = ComponentsMapper.physics.get(entity).getBody();
            bulletHandler.getCollisionWorld().addRigidBody(body);
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
        bulletHandler.dispose();
        for (Entity physical : physicals) {
            ComponentsMapper.physics.get(physical).dispose();
        }
    }

    public btDiscreteDynamicsWorld getCollisionWorld() {
        return bulletHandler.getCollisionWorld();
    }

    @Override
    public void onStaticEnvironmentObjectHardCollision(Entity entity) {
        PhysicsComponent envPhysicsComponent = ComponentsMapper.physics.get(entity);
        envPhysicsComponent.getBody().setAngularFactor(1);
        if (envPhysicsComponent.isStatic()) {
            envPhysicsComponent.setStatic(false);
            environmentObjectStaticValueChanged(entity, false);
        }
        envPhysicsComponent.recalculateLocalInertia();
        getCollisionWorld().addRigidBody(envPhysicsComponent.getBody());
    }

    private void environmentObjectStaticValueChanged(Entity entity, boolean newValue) {
        for (PhysicsSystemEventsSubscriber subscriber : subscribers) {
            subscriber.onEnvironmentObjectStaticValueChange(newValue, entity);
        }
    }

    public void subscribeForEvents(PhysicsSystemEventsSubscriber subscriber) {
        if (subscribers.contains(subscriber)) return;
        subscribers.add(subscriber);
    }
}
