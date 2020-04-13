package com.gadarts.war.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class CameraComponent implements Component, Pool.Poolable {
	private PerspectiveCamera camera;
	private Vector2 manipulationSpeed = new Vector2();
	private Entity target;

	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	@Override
	public void reset() {
		camera = null;
	}

	public PerspectiveCamera getCamera() {
		return camera;
	}

	public void setCamera(PerspectiveCamera camera) {
		this.camera = camera;
	}

	public Vector2 getManipulationSpeed(Vector2 out) {
		return out.set(manipulationSpeed);
	}

	public void setManipulationSpeed(Vector2 manipulationSpeed) {
		this.manipulationSpeed.set(manipulationSpeed);
	}
}
