package com.gadarts.war.components.model;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Pool;

public class ModelInstanceComponent implements Component, Pool.Poolable {
    private ModelInstance modelInstance;
    private BoundingBox boundingBox = new BoundingBox();


    public void init(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
        modelInstance.calculateBoundingBox(boundingBox);
    }

    @Override
    public void reset() {
        modelInstance = null;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public BoundingBox getBoundingBox(BoundingBox auxBoundBox) {
        return auxBoundBox.set(boundingBox);
    }
}
