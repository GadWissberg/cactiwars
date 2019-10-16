package com.gadarts.war.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Pool;

public class CameraComponent implements Component, Pool.Poolable {
    private PerspectiveCamera camera;
    private float manipulationSpeed;

    public Entity getTarget() {
        return target;
    }

    private Entity target;

    @Override
    public void reset() {
        camera = null;
    }

    public void setCamera(PerspectiveCamera camera) {
        this.camera = camera;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }


    public float getManipulationSpeed() {
        return manipulationSpeed;
    }

    public void setManipulationSpeed(float manipulationSpeed) {
        this.manipulationSpeed = manipulationSpeed;
    }
}
