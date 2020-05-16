package com.gadarts.war.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.gadarts.war.systems.physics.CollisionShapesDebugDrawing;
import com.gadarts.war.systems.physics.PhysicsSystemEventsSubscriber;

public class LightsSystem extends GameEntitySystem implements PhysicsSystemEventsSubscriber, EntityListener {
	private Environment environment;

	@Override
	public void onResize(int width, int height) {

	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(this);
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		DirectionalLight light = new DirectionalLight();
		light.direction.set(-0.5f, -1, -0.5f);
		light.color.set(0.4f, 0.4f, 0.4f, 1f);
		environment.add(light);
	}

	@Override
	public void collisionShapesDrawingInitialized(CollisionShapesDebugDrawing collisionShapesDebugDrawingMethod) {

	}

	@Override
	public void onEnvironmentObjectStaticValueChange(boolean b, Entity entity) {

	}

	@Override
	public void entityAdded(Entity entity) {

	}

	@Override
	public void entityRemoved(Entity entity) {

	}

	public Environment getEnvironment() {
		return environment;
	}
}
