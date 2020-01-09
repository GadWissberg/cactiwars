package com.gadarts.war.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.shared.SharedC.Camera;
import com.gadarts.war.GameC;
import com.gadarts.war.GameSettings;
import com.gadarts.war.components.CameraComponent;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.character.MovementState;
import com.gadarts.war.screens.BattleScreen;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;

import static com.gadarts.war.systems.physics.PhysicsSystem.auxMatrix;

/**
 * Defines the camera's movement and behaviour.
 */
public class CameraSystem extends EntitySystem implements PlayerSystemEventsSubscriber {
    private static Vector3 auxVector31 = new Vector3();
    private static Vector3 auxVector32 = new Vector3();
    private static Vector3 auxVector2 = new Vector3();
    private CameraInputController debugInputProcessor;
    private Entity cameraEntity;
    private float zoomAcceleration;
    private float zoomSpeed;

    public static CameraInputController createAndSetDebugInputProcessor(PerspectiveCamera camera) {
        CameraInputController processor = new CameraInputController(camera);
        processor.autoUpdate = true;
        Gdx.input.setInputProcessor(processor);
        return processor;
    }

    private CameraComponent createCameraComponent(PooledEngine pooledEngine) {
        CameraComponent cameraComponent = (pooledEngine).createComponent(CameraComponent.class);
        cameraComponent.setCamera(createCamera());
        return cameraComponent;
    }

    public static PerspectiveCamera createCamera() {
        PerspectiveCamera cam = new PerspectiveCamera(Camera.FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.near = Camera.NEAR;
        cam.far = Camera.FAR;
        cam.update();
        return cam;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        cameraEntity = new Entity();
        PooledEngine pooledEngine = (PooledEngine) engine;
        CameraComponent cameraComponent = createCameraComponent(pooledEngine);
        cameraEntity.add(cameraComponent);
        engine.addEntity(cameraEntity);
        if (GameSettings.SPECTATOR) debugInputProcessor = createAndSetDebugInputProcessor(cameraComponent.getCamera());
    }

    private void followTarget(CameraComponent cameraComponent, float deltaTime) {
        Entity target = cameraComponent.getTarget();
        PerspectiveCamera camera = cameraComponent.getCamera();
        handleCameraManipulation(deltaTime, target);
        camera.update();
    }

    private void handleCameraManipulation(float deltaTime, Entity target) {
        Vector3 linearVelocity = ComponentsMapper.physics.get(target).getBody().getLinearVelocity();
        float linearSpeed = linearVelocity.len2();
        ComponentsMapper.physics.get(target).getMotionState().getWorldTransform(auxMatrix);
        if (!linearVelocity.isCollinear(auxVector31.set(0, -1, 0), 0.1f) && Math.abs(linearSpeed) >= 0.1f)
            handleCameraManOnMovement(deltaTime, target);
        else handleCameraManOnIdle(deltaTime);
    }

    private void handleCameraManOnMovement(float deltaTime, Entity target) {
        Vector3 targetPos = auxMatrix.getTranslation(auxVector2);
        CameraComponent cameraComponent = ComponentsMapper.camera.get(this.cameraEntity);
        Vector3 cameraPosition = cameraComponent.getCamera().position;
        if (auxVector31.set(cameraPosition).dst2(targetPos) < GameC.Camera.MAX_ZOOM_DISTANCE) {
            Vector3 directionFromTarget = auxVector32.set(targetPos).sub(cameraPosition).nor();
            cameraPosition.add(directionFromTarget.scl(-deltaTime));
        }
        handleCameraFrontOffset(deltaTime, target, cameraPosition, targetPos);
    }

    private void handleCameraFrontOffset(float deltaTime, Entity target, Vector3 cameraPosition, Vector3 targetPos) {
        CameraComponent cameraComponent = ComponentsMapper.camera.get(cameraEntity);
        cameraComponent.setManipulationSpeed(cameraComponent.getManipulationSpeed() + 0.1f);
        Vector3 targetDirection = auxVector31.set(1, 0, 0).rot(auxMatrix).nor();
        float newX = calculateNewXForTargetFollowing(deltaTime, target, targetPos, targetDirection);
        float newZ = calculateNewZForTargetFollowing(deltaTime, target, targetPos, targetDirection);
        cameraPosition.set(newX, cameraPosition.y, newZ);
    }

    private float calculateNewXForTargetFollowing(float deltaTime, Entity target, Vector3 targetPos, Vector3 targetDir) {
        float linearSpeed = ComponentsMapper.physics.get(target).getBody().getLinearVelocity().len2();
        float minX = targetPos.x - GameC.Camera.MAX_X_FRONT_OFFSET;
        float maxX = targetPos.x + GameC.Camera.MAX_X_FRONT_OFFSET;
        MovementState movementState = ComponentsMapper.characters.get(target).getMovementState();
        boolean isAccelerating = movementState == MovementState.ACCELERATING && linearSpeed > 1f;
        int coef = isAccelerating ? 1 : -1;
        CameraComponent cameraComponent = ComponentsMapper.camera.get(cameraEntity);
        float manipulationSpeed = cameraComponent.getManipulationSpeed();
        Vector3 cameraPosition = cameraComponent.getCamera().position;
        float newX = cameraPosition.x + coef * targetDir.x * Math.min(linearSpeed, manipulationSpeed) * deltaTime;
        return MathUtils.clamp(newX, minX, maxX);
    }

    private float calculateNewZForTargetFollowing(float deltaTime, Entity target, Vector3 targetPos, Vector3 targetDir) {
        CameraComponent cameraComponent = ComponentsMapper.camera.get(cameraEntity);
        float linearSpeed = ComponentsMapper.physics.get(target).getBody().getLinearVelocity().len2();
        float minZ = targetPos.z + GameC.Camera.MIN_Z_FRONT_OFFSET;
        float maxZ = targetPos.z + GameC.Camera.MAX_Z_FRONT_OFFSET;
        MovementState movementState = ComponentsMapper.characters.get(target).getMovementState();
        boolean isAccelerating = movementState == MovementState.ACCELERATING && linearSpeed > 1f;
        Vector3 cameraPosition = ComponentsMapper.camera.get(cameraEntity).getCamera().position;
        int coef = isAccelerating ? 1 : -1;
        float manipulationSpeed = cameraComponent.getManipulationSpeed();
        float newX = cameraPosition.z + coef * targetDir.z * Math.min(linearSpeed, manipulationSpeed) * deltaTime;
        return MathUtils.clamp(newX, minZ, maxZ);
    }

    private void handleCameraManOnIdle(float deltaTime) {
        Vector3 targetPos = auxMatrix.getTranslation(auxVector2);
        Vector3 cameraPosition = ComponentsMapper.camera.get(this.cameraEntity).getCamera().position;
        if (auxVector31.set(cameraPosition).dst2(targetPos) > GameC.Camera.MIN_ZOOM_DISTANCE) {
            ComponentsMapper.camera.get(cameraEntity).setManipulationSpeed(0);
            Vector3 directionFromTarget = auxVector32.set(auxVector31.set(targetPos.x,
                    targetPos.y + Camera.TARGET_Y_MIN_OFFSET, targetPos.z + Camera.TARGET_Z_MIN_OFFSET))
                    .sub(cameraPosition).nor();
            cameraPosition.add(directionFromTarget.scl(deltaTime));
        }
    }

    @Override
    public void update(float deltaTime) {
        if (BattleScreen.isPaused()) return;
        super.update(deltaTime);
        if (debugInputProcessor != null) debugInputProcessor.update();
        CameraComponent cameraComponent = ComponentsMapper.camera.get(cameraEntity);
        if (!GameSettings.SPECTATOR && cameraComponent.getTarget() != null) {
            followTarget(cameraComponent, deltaTime);
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
        position.set(targetPos.x, targetPos.y + Camera.TARGET_Y_MIN_OFFSET, targetPos.z + Camera.TARGET_Z_MIN_OFFSET);
        camera.lookAt(targetPos);
        camera.up.set(0, 1, 0);
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
}
