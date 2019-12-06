package com.gadarts.war.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.gadarts.shared.definitions.PointLightDefinition;

public class PointLightComponent implements Pool.Poolable, Component {
    private PointLightDefinition definition;
    private Entity parent;

    public Entity getParent() {
        return parent;
    }

    public PointLightDefinition getDefinition() {
        return definition;
    }

    @Override
    public void reset() {

    }

    public void init(PointLightDefinition pointLightDefinition, Entity parent) {
        this.definition = pointLightDefinition;
        this.parent = parent;
    }
}
