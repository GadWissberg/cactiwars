package com.gadarts.war.factories.recycle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.utils.Queue;

import java.util.HashMap;

public class CollisionShapesPool {
    private String DEBUG_MODEL_INSTANCE_POOL = "Collision Shape Pool";
    @SuppressWarnings("FieldCanBeLocal")
    private String DEBUG_OBTAINED = "Obtained a new collision shape:%s, left free: %d.";
    @SuppressWarnings("FieldCanBeLocal")
    private String DEBUG_FREED = "Freed a new model instance: %s, left free: %d.";
    @SuppressWarnings("FieldCanBeLocal")
    private String DEBUG_CREATING = "Creating a new collision shape: %s";

    private HashMap<String, Queue<btCollisionShape>> instancesMap = new HashMap<>();

    public btCollisionShape obtain(String name) {
        btCollisionShape result;
        if (instancesMap.containsKey(name)) {
            result = obtainFromQueue(name);
        } else {
            result = createNewQueueAndInstance(name);
        }
        return result;
    }

    private btCollisionShape obtainFromQueue(String name) {
        btCollisionShape result;
        Queue<btCollisionShape> instances = instancesMap.get(name);
        if (instances.isEmpty()) result = createNewInstance(name);
        else {
            result = instances.removeFirst();
            logObtainFromQueue(name, result, instances);
        }
        return result;
    }

    private btCollisionShape createNewInstance(String name) {
        btCompoundShape result;
        Gdx.app.debug(DEBUG_MODEL_INSTANCE_POOL, String.format(DEBUG_CREATING, name));
        result = new btCompoundShape(false);
        return result;
    }

    public void free(btCollisionShape instance, String name) {
        if (instancesMap.containsKey(name)) {
            Queue<btCollisionShape> instances = instancesMap.get(name);
            instances.addFirst(instance);
            if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
                Gdx.app.debug(DEBUG_MODEL_INSTANCE_POOL, String.format(DEBUG_FREED, name, instances.size));
            }
        }
    }

    private void logObtainFromQueue(String name, Object result, Queue<btCollisionShape> instances) {
        if (Gdx.app.getLogLevel() == Gdx.app.LOG_DEBUG) {
            String tag = result.getClass().getCanonicalName() + DEBUG_MODEL_INSTANCE_POOL;
            Gdx.app.debug(tag, String.format(DEBUG_OBTAINED, name, instances.size));
        }
    }

    private btCollisionShape createNewQueueAndInstance(String name) {
        btCollisionShape result;
        Queue<btCollisionShape> instances = new Queue<>();
        instancesMap.put(name, instances);
        result = createNewInstance(name);
        return result;
    }
}
