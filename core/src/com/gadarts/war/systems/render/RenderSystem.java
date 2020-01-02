package com.gadarts.war.systems.render;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gadarts.shared.definitions.PointLightDefinition;
import com.gadarts.war.BattleScreen;
import com.gadarts.war.GameSettings;
import com.gadarts.war.GameShaderProvider;
import com.gadarts.war.components.CameraComponent;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.EnvironmentObjectComponent;
import com.gadarts.war.components.PointLightComponent;
import com.gadarts.war.components.model.ModelInstanceComponent;
import com.gadarts.war.systems.physics.CollisionShapesDebugDrawing;
import com.gadarts.war.systems.physics.PhysicsSystemEventsSubscriber;
import com.gadarts.war.systems.render.shadow.ShadowRenderer;

import java.util.List;

public class RenderSystem extends EntitySystem implements PhysicsSystemEventsSubscriber, EntityListener {
    private static Vector3 auxVector31 = new Vector3();
    private static Vector3 auxVector32 = new Vector3();
    private static BoundingBox auxBoundingBox1 = new BoundingBox();

    private ModelBatch modelBatch;
    private PerspectiveCamera camera;
    private ImmutableArray<Entity> modelInstanceEntities;
    private Environment environment;
    private ShadowRenderer shadowRenderer;
    private RenderingDebugHandler renderingDebugHandler;
    private FrameBuffer fbo;
    private ModelBatch depthBatch = new ModelBatch(new CelDepthShaderProvider());
    private SpriteBatch spriteBatch = new SpriteBatch();
    private ShaderProgram lineShader = new CelLineShaderProgram();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        renderingDebugHandler = new RenderingDebugHandler();
        GameShaderProvider shaderProvider = new GameShaderProvider();
        modelBatch = new ModelBatch(shaderProvider);
        Entity cameraEntity = engine.getEntitiesFor(Family.all(CameraComponent.class).get()).get(0);
        camera = ComponentsMapper.camera.get(cameraEntity).getCamera();
        modelInstanceEntities = engine.getEntitiesFor(Family.all(ModelInstanceComponent.class).get());
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.1f, 1f));
        shadowRenderer = new ShadowRenderer(environment);
        engine.addEntityListener(this);
        if (GameSettings.CEL_SHADING) {
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                    true);
        }
    }


    @Override
    public void update(float deltaTime) {
        if (!BattleScreen.isPaused()) {
            super.update(deltaTime);
            if (GameSettings.CEL_SHADING) {
                fbo.begin();
                Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
                Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
                depthBatch.begin(camera);
                renderInstances(depthBatch, true, null, deltaTime);
                depthBatch.end();
                fbo.end();
            }
            render(deltaTime, null);
            if (GameSettings.CEL_SHADING) {
                spriteBatch.setShader(lineShader);
                spriteBatch.begin();
                spriteBatch.draw(fbo.getColorBufferTexture(), 0, 0, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1, 1, 0, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);
                spriteBatch.end();
                spriteBatch.setShader(null);
            }
        }
    }

    public void render(float deltaTime, FrameBuffer frameBuffer) {
        renderShadows();
        if (frameBuffer != null) {
            frameBuffer.begin();
        }
        initializeDisplay();
        renderingDebugHandler.resetCounter();
        modelBatch.begin(camera);
        renderInstances(modelBatch, true, environment, deltaTime);
        modelBatch.end();
        renderingDebugHandler.renderCollisionShapes(camera);
        if (frameBuffer != null) {
            frameBuffer.end();
        }
    }

    private void renderShadows() {
        if (GameSettings.DRAWING_SKIPPING_MODE && GameSettings.SKIP_DRAW_SHADOWS) return;
        shadowRenderer.begin(camera);
        renderInstances(shadowRenderer.getShadowBatch(), false, null, -1);
        shadowRenderer.end();
    }


    private void renderInstances(ModelBatch batch, boolean renderGround, Environment environment, float deltaTime) {
        for (Entity entity : modelInstanceEntities) {
            ModelInstance modelInstance = ComponentsMapper.modelInstance.get(entity).getModelInstance();
            animate(deltaTime, entity);
            if (isVisible(camera, entity))
                if (!shouldSkipRender(entity))
                    if (renderGround || !ComponentsMapper.ground.has(entity))
                        renderInstance(batch, environment, modelInstance);
        }
    }

    private void animate(float deltaTime, Entity entity) {
        if (!BattleScreen.isPaused() && ComponentsMapper.animations.has(entity)) {
            ComponentsMapper.animations.get(entity).getController().update(deltaTime);
        }
    }

    private void renderInstance(ModelBatch batch, Environment environment, ModelInstance modelInstance) {
        if (environment != null) batch.render(modelInstance, environment);
        else batch.render(modelInstance);
        renderingDebugHandler.inc();
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
        modelInstanceComponent.getModelInstance().transform.getTranslation(auxVector31);
        auxVector31.add(modelInstanceComponent.getBoundingBox(auxBoundingBox1).getCenter(auxVector32));
        auxBoundingBox1.getDimensions(auxVector32);
        auxVector32.x = Math.max(auxVector32.x, Math.max(auxVector32.y, auxVector32.z));
        auxVector32.y = Math.max(auxVector32.x, Math.max(auxVector32.y, auxVector32.z));
        auxVector32.z = Math.max(auxVector32.x, Math.max(auxVector32.y, auxVector32.z));
        return camera.frustum.boundsInFrustum(auxVector31, auxVector32);
    }

    public void initializeDisplay() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        int coverageSampling = Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | coverageSampling);
        Gdx.gl.glClearColor(1, 0, 0, 1);
    }

    public void dispose() {
        modelBatch.dispose();
        shadowRenderer.dispose();
        if (GameSettings.CEL_SHADING) {
            depthBatch.dispose();
            spriteBatch.dispose();
            fbo.dispose();
            lineShader.dispose();
        }
    }

    public int getNumberOfVisible() {
        return renderingDebugHandler.getNumberOfVisible();
    }

    @Override
    public void collisionShapesDrawingInitialized(CollisionShapesDebugDrawing collisionShapesDebugDrawingMethod) {
        renderingDebugHandler.setCollisionShapesDrawing(collisionShapesDebugDrawingMethod);
    }

    @Override
    public void onEnvironmentObjectStaticValueChange(boolean newValue, Entity entity) {
        EnvironmentObjectComponent environmentObjectComponent = ComponentsMapper.environmentObject.get(entity);
        List<Entity> sourceLights = environmentObjectComponent.getSourceLights();
        for (Entity sourceLight : sourceLights) {
            Environment environment = getEngine().getSystem(RenderSystem.class).getEnvironment();
            environment.remove(ComponentsMapper.pointLights.get(sourceLight).getPointLightObject());
            getEngine().removeEntity(sourceLight);
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        if (ComponentsMapper.pointLights.has(entity)) {
            addPointLight(entity);
        }
    }

    private void addPointLight(Entity entity) {
        PointLightComponent plc = ComponentsMapper.pointLights.get(entity);
        PointLightDefinition definition = plc.getDefinition();
        Vector3 pos = ComponentsMapper.physics.get(plc.getParent()).getMotionState().getWorldTranslation(auxVector31);
        PointLight pointLight = new PointLight().set(0.8f, 0.8f, 0.8f, pos.x + definition.getOffsetX(),
                pos.y + definition.getOffsetY(), pos.z + definition.getOffsetZ(), definition.getIntensity());
        plc.setPointLightObject(pointLight);
        environment.add(pointLight);
    }

    @Override
    public void entityRemoved(Entity entity) {

    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getNumberOfModelInstances() {
        return modelInstanceEntities.size();
    }

    public GameShaderProvider getShaderProvider() {
        return (GameShaderProvider) modelBatch.getShaderProvider();
    }
}
