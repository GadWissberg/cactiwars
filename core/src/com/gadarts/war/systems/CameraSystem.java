package com.gadarts.war.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
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
	public static final int CAMERA_MAX_DISTANCE = 6;
	private static final Vector3 auxVector31 = new Vector3();
	private static final Vector3 auxVector32 = new Vector3();
	private static final Vector3 auxVector34 = new Vector3();
	public static final int DISTANCE_WHEN_MOVING = 4;
	public static final int MAX_CAMERA_NORTH = -2;
	public static final int MAX_CAMERA_SOUTH = 10;
	public static final int CAMERA_ZOOM_DELAY = 1000;
	public static final int DEGREES_OF_DISTANCE_VECTOR_WHEN_MOVING = 45;
	public static final int DEGREES_OF_DISTANCE_VECTOR_NOT_MOVING = 90;
	public static final int CAMERA_MIN_DISTANCE = 2;
	private static final float CAMERA_Z_RELATIVE_OFFSET = 4;
	private static final Vector3 auxVector35 = new Vector3();
	private CameraInputController debugInputProcessor;
	private Entity cameraEntity;
	private long lastZoomChange;
	private boolean zoomActive;

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
		Vector3 targetRealPos = ComponentsMapper.physics.get(target).getMotionState().getWorldTranslation(auxVector32);
		Vector3 targetRelativePosition = auxVector33.set(targetRealPos.x, camPos.y, targetRealPos.z);
		targetRelativePosition.add(0, 0, CAMERA_Z_RELATIVE_OFFSET);
		manipulateCamera(auxVector34, targetRelativePosition, camPos, targetRealPos);
		camPos.interpolate(targetRelativePosition, 0.15f, Interpolation.smooth2);
		ComponentsMapper.camera.get(cameraEntity).getCamera().position.set(camPos);
	}

	private void manipulateCamera(Vector3 velocity, Vector3 targetRelativePosition, Vector3 camPos, Vector3 targetRealPos) {
		if (Math.abs(velocity.len2()) > 0.04) {
			manipulateCameraWhenMoving(velocity, targetRelativePosition, targetRealPos);
		} else {
			manipulateCameraZoom(velocity, targetRelativePosition, targetRealPos);
		}

	}

	private void manipulateCameraZoom(Vector3 velocity, Vector3 targetRelativePosition, Vector3 targetRealPos) {
		int distanceFromTarget = CAMERA_MAX_DISTANCE;
		int degreesOfDistanceVector = DEGREES_OF_DISTANCE_VECTOR_WHEN_MOVING;
		if (zoomActive) {
			distanceFromTarget = CAMERA_MIN_DISTANCE;
			degreesOfDistanceVector = DEGREES_OF_DISTANCE_VECTOR_NOT_MOVING;
		} else if (TimeUtils.timeSinceMillis(lastZoomChange) > CAMERA_ZOOM_DELAY) {
			zoomActive = true;
		} else {
			targetRelativePosition.add(velocity.x, 0, CAMERA_Z_RELATIVE_OFFSET);
		}
		Vector3 positionNearAboveTarget = auxVector35.set(targetRealPos).add(auxVector35.set(1, 0, 0)
				.rotate(Vector3.Z, degreesOfDistanceVector).nor().scl(distanceFromTarget));
		targetRelativePosition.y = positionNearAboveTarget.y;
	}

	private void manipulateCameraWhenMoving(Vector3 velocity, Vector3 targetRelativePosition, Vector3 targetRealPos) {
		velocity.scl(DISTANCE_WHEN_MOVING);
		float z = MathUtils.clamp(velocity.z >= 0 ? velocity.z * 2 : velocity.z, MAX_CAMERA_NORTH, MAX_CAMERA_SOUTH);
		targetRelativePosition.add(velocity.x, 0, z);
		zoomActive = false;
		lastZoomChange = TimeUtils.millis();
		Vector3 positionNearAboveTarget = auxVector35.set(targetRealPos).add(auxVector35.set(1, 0, 0)
				.rotate(Vector3.Z, DEGREES_OF_DISTANCE_VECTOR_WHEN_MOVING).nor().scl(CAMERA_MAX_DISTANCE));
		targetRelativePosition.y = positionNearAboveTarget.y;
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
