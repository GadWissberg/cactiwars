package com.gadarts.war.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

public class CharacterAdditionalComponent implements Pool.Poolable, Component {
    private Entity parent;

    @Override
    public void reset() {

    }

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Entity getParent() {
        return parent;
    }
}
