package com.gadarts.war.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gadarts.shared.definitions.PointLightDefinition;
import com.gadarts.war.GameSettings;
import com.gadarts.war.GameShaderProvider;
import com.gadarts.war.components.CameraComponent;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.PointLightComponent;
import com.gadarts.war.components.model.ModelInstanceComponent;
import com.gadarts.war.components.physics.PhysicsComponent;
import com.gadarts.war.systems.physics.CollisionShapesDebugDrawing;
import com.gadarts.war.systems.physics.PhysicsSystemEventsSubscriber;

public class RenderSystem extends EntitySystem implements PhysicsSystemEventsSubscriber, EntityListener {
    private static Vector3 auxVector31 = new Vector3();
    private static Vector3 auxVector32 = new Vector3();
    private static BoundingBox auxBoundingBox1 = new BoundingBox();

    private ModelBatch modelBatch;
    private PerspectiveCamera camera;
    private ImmutableArray<Entity> modelInstanceEntities;
    private int numberOfVisible;
    private CollisionShapesDebugDrawing collisionShapesDebugDrawingMethod;
    private Environment environment;
    private GameShaderProvider shaderProvider;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        shaderProvider = new GameShaderProvider();
        modelBatch = new ModelBatch(shaderProvider);
        Entity cameraEntity = engine.getEntitiesFor(Family.all(CameraComponent.class).get()).get(0);
        camera = ComponentsMapper.camera.get(cameraEntity).getCamera();
        modelInstanceEntities = engine.getEntitiesFor(Family.all(ModelInstanceComponent.class).get());
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0, 0, 0, 1f));
        environment.add(new DirectionalLight().set(0.3f, 0.3f, 0.3f, -1, -0.7f, -1));
        engine.addEntityListener(this);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        initializeDisplay();
        numberOfVisible = 0;
        modelBatch.begin(camera);
        renderAllInstances();
        modelBatch.end();
        if (collisionShapesDebugDrawingMethod != null) collisionShapesDebugDrawingMethod.drawCollisionShapes(camera);
    }

    private void renderAllInstances() {
        for (Entity entity : modelInstanceEntities) {
            ModelInstance modelInstance = ComponentsMapper.modelInstance.get(entity).getModelInstance();
            if (isVisible(camera, entity))
                if (!shouldSkipRender(entity)) {
                    modelBatch.render(modelInstance, environment);
                    numberOfVisible++;
                }
        }
    }

    private boolean shouldSkipRender(Entity entity) {
        if (!GameSettings.DRAWING_SKIPPING_MODE) return false;
        boolean groundCheck = GameSettings.SKIP_GROUND_DRAWING && ComponentsMapper.ground.has(entity);
        boolean characterCheck = GameSettings.SKIP_CHARACTER_DRAWING && ComponentsMapper.characters.has(entity);
        boolean envCheck = GameSettings.SKIP_ENV_OBJECT_DRAWING && ComponentsMapper.environmentObject.has(entity);
        return groundCheck || characterCheck || envCheck;
    }

    private boolean isVisible(PerspectiveCamera camera, Entity entity) {
        ModelInstanceComponent modelInstanceComponent = ComponentsMapper.modelInstance.get(entity);
        ModelInstance modelInstance = modelInstanceComponent.getModelInstance();
        modelInstance.transform.getTranslation(auxVector31);
        auxVector31.add(modelInstanceComponent.getBoundingBox(auxBoundingBox1).getCenter(auxVector32));
        return camera.frustum.boundsInFrustum(auxVector31, auxBoundingBox1.getDimensions(auxVector32));
    }

    private void initializeDisplay() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        int coverageSampling = Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | coverageSampling);
        Gdx.gl.glClearColor(0, 0, 0, 1);
    }

    public void dispose() {
        modelBatch.dispose();
    }

    public int getNumberOfVisible() {
        return numberOfVisible;
    }

    @Override
    public void collisionShapesDrawingInitialized(CollisionShapesDebugDrawing collisionShapesDebugDrawingMethod) {
        this.collisionShapesDebugDrawingMethod = collisionShapesDebugDrawingMethod;
    }

    @Override
    public void entityAdded(Entity entity) {
        if (ComponentsMapper.pointLights.has(entity)) {
            PointLightComponent pointLightComponent = ComponentsMapper.pointLights.get(entity);
            PointLightDefinition definition = pointLightComponent.getDefinition();
            PhysicsComponent parent = ComponentsMapper.physics.get(pointLightComponent.getParent());
            Vector3 pos = parent.getMotionState().getWorldTranslation(auxVector31);
            environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, pos.x + definition.getOffsetX(),
                    pos.y + 2, pos.z + definition.getOffsetZ(), 5));
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }
}
