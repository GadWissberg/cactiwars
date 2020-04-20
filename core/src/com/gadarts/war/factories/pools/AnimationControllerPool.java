package com.gadarts.war.factories.pools;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Queue;

import java.util.HashMap;

public class AnimationControllerPool {

	protected HashMap<String, Queue<AnimationController>> instancesMap = new HashMap<>();

	public AnimationController obtain(String name, ModelInstance model) {
		AnimationController result;
		if (instancesMap.containsKey(name)) {
			result = obtainFromQueue(name, model);
		} else {
			result = createNewQueueAndInstance(name, model);
		}
		return result;
	}

	private AnimationController obtainFromQueue(String name, ModelInstance model) {
		AnimationController result;
		Queue<AnimationController> instances = instancesMap.get(name);
		if (instances.isEmpty()) result = createNewInstance(name, model);
		else {
			result = instances.removeFirst();
		}
		return result;
	}

	private AnimationController createNewInstance(String name, ModelInstance model) {
		AnimationController result;
		result = new AnimationController(model);
		return result;
	}

	public void free(AnimationController instance, String name) {
		if (instancesMap.containsKey(name)) {
			Queue<AnimationController> instances = instancesMap.get(name);
			instances.addFirst(instance);
		}
	}

	private AnimationController createNewQueueAndInstance(String name, ModelInstance model) {
		AnimationController result;
		Queue<AnimationController> instances = new Queue<>();
		instancesMap.put(name, instances);
		result = createNewInstance(name, model);
		return result;
	}
}
