package com.gadarts.war.factories.pools;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Queue;

import java.util.HashMap;

public class ModelInstancesPool {
	private static final String DEBUG_MODEL_INSTANCE_POOL = "Model Instances Pool";
	private static final String DEBUG_OBTAINED = "Obtained a new model instance:%s, left free: %d.";
	private static final String DEBUG_FREED = "Freed a new model instance: %s, left free: %d.";
	private static final String DEBUG_CREATING = "Creating a new model instance: %s";

	protected HashMap<String, Queue<ModelInstance>> instancesMap = new HashMap<>();

	public ModelInstance obtain(String name, Model model) {
		ModelInstance result;
		if (instancesMap.containsKey(name)) {
			result = obtainFromQueue(name, model);
		} else {
			result = createNewQueueAndInstance(name, model);
		}
		return result;
	}

	private ModelInstance obtainFromQueue(String name, Model model) {
		ModelInstance result;
		Queue<ModelInstance> instances = instancesMap.get(name);
		if (instances.isEmpty()) result = createNewInstance(name, model);
		else {
			result = instances.removeFirst();
			logObtainFromQueue(name, result, instances);
		}
		return result;
	}

	private ModelInstance createNewInstance(String name, Model model) {
		ModelInstance result;
		Gdx.app.debug(DEBUG_MODEL_INSTANCE_POOL, String.format(DEBUG_CREATING, name));
		result = new ModelInstance(model);
		return result;
	}

	public void free(ModelInstance instance, String name) {
		if (instancesMap.containsKey(name)) {
			Queue<ModelInstance> instances = instancesMap.get(name);
			instances.addFirst(instance);
			if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
				Gdx.app.debug(DEBUG_MODEL_INSTANCE_POOL, String.format(DEBUG_FREED, name, instances.size));
			}
		}
	}

	private void logObtainFromQueue(String name, Object result, Queue<ModelInstance> instances) {
		if (Gdx.app.getLogLevel() == Gdx.app.LOG_DEBUG) {
			String tag = result.getClass().getCanonicalName() + DEBUG_MODEL_INSTANCE_POOL;
			Gdx.app.debug(tag, String.format(DEBUG_OBTAINED, name, instances.size));
		}
	}

	private ModelInstance createNewQueueAndInstance(String name, Model model) {
		ModelInstance result;
		Queue<ModelInstance> instances = new Queue<>();
		instancesMap.put(name, instances);
		result = createNewInstance(name, model);
		return result;
	}
}
