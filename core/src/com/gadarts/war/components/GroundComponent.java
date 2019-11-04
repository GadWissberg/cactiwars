package com.gadarts.war.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class GroundComponent implements Component, Pool.Poolable {
    private boolean physical;

    public void init(boolean isPhysical) {
        this.physical = isPhysical;
    }

    @Override
    public void reset() {
        physical = true;
    }

    public boolean isPhysical() {
        return physical;
    }
}
