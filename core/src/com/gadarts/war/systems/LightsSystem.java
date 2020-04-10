package com.gadarts.war.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.gadarts.shared.definitions.PointLightDefinition;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.LightEmitterComponent;
import com.gadarts.war.components.PointLightComponent;
import com.gadarts.war.components.physics.MotionState;
import com.gadarts.war.systems.physics.CollisionShapesDebugDrawing;
import com.gadarts.war.systems.physics.PhysicsSystemEventsSubscriber;
import com.gadarts.war.systems.render.RenderSystem;

import java.util.List;

public class LightsSystem extends GameEntitySystem implements PhysicsSystemEventsSubscriber, EntityListener {
	private static final Vector3 auxVector = new Vector3();
	private ImmutableArray<Entity> lights;
	private Environment environment;

	@Override
	public void onResize(int width, int height) {

	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		lights = engine.getEntitiesFor(Family.all(PointLightComponent.class).get());
		engine.addEntityListener(this);
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		for (Entity light : lights) {
			positionLight(light);
		}
	}

	private void positionLight(Entity light) {
		PointLightComponent component = ComponentsMapper.pointLight.get(light);
		MotionState motionState = ComponentsMapper.physics.get(component.getParent()).getMotionState();
		Vector3 parentPosition = motionState.getWorldTranslation(auxVector);
		PointLightDefinition definition = component.getDefinition();
		parentPosition.add(definition.getOffsetX(), definition.getOffsetY(), definition.getOffsetZ());
		component.getPointLightObject().position.set(parentPosition);
	}

	@Override
	public void collisionShapesDrawingInitialized(CollisionShapesDebugDrawing collisionShapesDebugDrawingMethod) {

	}

	@Override
	public void onEnvironmentObjectStaticValueChange(boolean newValue, Entity entity) {
		if (!ComponentsMapper.lightEmitter.has(entity)) return;
		LightEmitterComponent environmentObjectComponent = ComponentsMapper.lightEmitter.get(entity);
		removeLightsFromEmitter(environmentObjectComponent);
	}

	private void removeLightsFromEmitter(LightEmitterComponent emitterComponent) {
		List<Entity> sourceLights = emitterComponent.getSourceLights();
		for (Entity sourceLight : sourceLights) {
			removeLight(sourceLight);
		}
	}

	private void removeLight(Entity sourceLight) {
		Environment environment = getEngine().getSystem(RenderSystem.class).getEnvironment();
		PointLight pointLightObject = ComponentsMapper.pointLight.get(sourceLight).getPointLightObject();
		environment.remove(pointLightObject);
		Pools.get(PointLight.class).free(pointLightObject);
		getEngine().removeEntity(sourceLight);
		Gdx.app.log("REMOVED", "Free:" + Pools.get(PointLight.class).getFree() + ", Peak:" + Pools.get(PointLight.class).peak);
	}

	@Override
	public void entityAdded(Entity entity) {
		if (ComponentsMapper.pointLight.has(entity)) {
			addPointLight(entity);
		}
	}

	private void addPointLight(Entity entity) {
		PointLightComponent plc = ComponentsMapper.pointLight.get(entity);
		PointLightDefinition definition = plc.getDefinition();
		Vector3 pos = ComponentsMapper.physics.get(plc.getParent()).getMotionState().getWorldTranslation(auxVector);
		PointLight pointLight = Pools.get(PointLight.class).obtain();
		pointLight.set(0.8f, 0.8f, 0.8f, pos.x + definition.getOffsetX(),
				pos.y + definition.getOffsetY(), pos.z + definition.getOffsetZ(), definition.getIntensity());
		plc.setPointLightObject(pointLight);
		environment.add(pointLight);
		Gdx.app.log("NEW", "Free:" + Pools.get(PointLight.class).getFree());
	}

	@Override
	public void entityRemoved(Entity entity) {
		if (ComponentsMapper.lightEmitter.has(entity)) {
			removeLightsFromEmitter(ComponentsMapper.lightEmitter.get(entity));
		}
	}

	public Environment getEnvironment() {
		return environment;
	}
}
