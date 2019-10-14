package com.gadarts.war.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gadarts.war.GameShaderProvider;
import com.gadarts.war.components.CameraComponent;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.model.ModelInstanceComponent;

public class RenderSystem extends EntitySystem {
    private static Vector3 auxVector31 = new Vector3();
    private static Vector3 auxVector32 = new Vector3();
    private static BoundingBox auxBoundingBox1 = new BoundingBox();

    private ModelBatch modelBatch;
    private GameShaderProvider shaderProvider;
    private PerspectiveCamera camera;
    private ImmutableArray<Entity> modelInstanceEntities;
    private int numberOfVisible;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        modelBatch = new ModelBatch();
        Entity cameraEntity = engine.getEntitiesFor(Family.all(CameraComponent.class).get()).get(0);
        camera = ComponentsMapper.camera.get(cameraEntity).getCamera();
        modelInstanceEntities = engine.getEntitiesFor(Family.all(ModelInstanceComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        initializeDisplay();
        numberOfVisible = 0;
        modelBatch.begin(camera);
        renderAllInstances();
        modelBatch.end();
    }

    private void renderAllInstances() {
        for (Entity entity : modelInstanceEntities) {
            ModelInstance modelInstance = ComponentsMapper.modelInstance.get(entity).getModelInstance();
            if (isVisible(camera, entity)) {
                modelBatch.render(modelInstance);
                numberOfVisible++;
            }
        }
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

}
