package com.gadarts.war.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.shared.SharedC.Camera;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.components.CameraComponent;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.screens.BattleScreen;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;

/**
 * Defines the camera's movement and behaviour.
 */
public class CameraSystem extends GameEntitySystem implements PlayerSystemEventsSubscriber {
	private final static Vector3 auxVector33 = new Vector3();
	private static final float CAMERA_Z_RELATIVE_OFFSET = 8;
	private static final Vector3 auxVector31 = new Vector3();
	private static final Vector3 auxVector32 = new Vector3();
	private static final Vector3 auxVector34 = new Vector3();
	private CameraInputController debugInputProcessor;
	private Entity cameraEntity;

	public static CameraInputController createAndSetDebugInputProcessor(PerspectiveCamera camera) {
		CameraInputController processor = new CameraInputController(camera);
		processor.autoUpdate = true;
		Gdx.input.setInputProcessor(processor);
		return processor;
	}

	public static PerspectiveCamera createCamera() {
		PerspectiveCamera cam = new PerspectiveCamera(Camera.FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.near = Camera.NEAR;
		cam.far = Camera.FAR;
		cam.update();
		return cam;
	}

	private CameraComponent createCameraComponent(PooledEngine pooledEngine) {
		CameraComponent cameraComponent = (pooledEngine).createComponent(CameraComponent.class);
		cameraComponent.setCamera(createCamera());
		return cameraComponent;
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		cameraEntity = new Entity();
		PooledEngine pooledEngine = (PooledEngine) engine;
		CameraComponent cameraComponent = createCameraComponent(pooledEngine);
		cameraEntity.add(cameraComponent);
		engine.addEntity(cameraEntity);
		if (DefaultGameSettings.SPECTATOR)
			debugInputProcessor = createAndSetDebugInputProcessor(cameraComponent.getCamera());
	}

	private void followTarget(CameraComponent cameraComponent) {
		Entity target = cameraComponent.getTarget();
		PerspectiveCamera camera = cameraComponent.getCamera();
		handleCameraManipulation(target);
		camera.update();
	}

	private void handleCameraManipulation(Entity target) {
		ComponentsMapper.physics.get(target).getBody().getInterpolationLinearVelocity(auxVector34);
		Vector3 camPos = auxVector31.set(ComponentsMapper.camera.get(cameraEntity).getCamera().position);
		Vector3 targetPosition = ComponentsMapper.physics.get(target).getMotionState().getWorldTranslation(auxVector32);
		auxVector33.set(targetPosition.x, camPos.y, targetPosition.z).add(0, 0, CAMERA_Z_RELATIVE_OFFSET);
		manipulateCamera(auxVector34);
		camPos.interpolate(auxVector33, 0.1f, Interpolation.circle);
		ComponentsMapper.camera.get(cameraEntity).getCamera().position.set(camPos);
	}

	private void manipulateCamera(Vector3 velocity) {
		if (Math.abs(velocity.len2()) > 0.01) {
			velocity.scl(4);
			auxVector33.add(velocity.x, 0, velocity.z);
		}
	}

	@Override
	public void update(float deltaTime) {
		if (BattleScreen.isPaused()) return;
		super.update(deltaTime);
		if (debugInputProcessor != null) debugInputProcessor.update();
		CameraComponent cameraComponent = ComponentsMapper.camera.get(cameraEntity);
		if (!DefaultGameSettings.SPECTATOR && cameraComponent.getTarget() != null) {
			followTarget(cameraComponent);
		}
	}

	/**
	 * Sets the camera to follow the given entity.
	 *
	 * @param target
	 */
	public void lockToTarget(Entity target) {
		CameraComponent cameraComponent = ComponentsMapper.camera.get(cameraEntity);
		cameraComponent.setTarget(target);
		Vector3 targetPos = ComponentsMapper.physics.get(target).getMotionState().getWorldTranslation(auxVector31);
		PerspectiveCamera camera = cameraComponent.getCamera();
		Vector3 position = camera.position;
		position.set(targetPos.x + 5, targetPos.y + Camera.TARGET_Y_MIN_OFFSET, targetPos.z + 2 * Camera.TARGET_Z_MIN_OFFSET);
		camera.up.set(0, 1, 0);
		camera.direction.rotate(Vector3.X, -45);
		camera.update();
	}

	@Override
	public void onMovementAccelerationBegan() {

	}

	@Override
	public void onMovementAccelerationStopped() {

	}

	public Entity getCameraEntity() {
		return cameraEntity;
	}

	@Override
	public void onResize(int width, int height) {

	}
}
