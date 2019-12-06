package com.gadarts.war.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentObjectComponent implements Component, Pool.Poolable {
    private List<Entity> sourceLights = new ArrayList<>();

    public void addSourceLight(Entity light) {
        sourceLights.add(light);
    }

    @Override
    public void reset() {
    }

    public List<Entity> getSourceLights() {
        return sourceLights;
    }
}
