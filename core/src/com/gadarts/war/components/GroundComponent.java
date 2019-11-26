package com.gadarts.war.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.gadarts.shared.SharedC;

public class GroundComponent implements Component, Pool.Poolable {
    private boolean physical;
    private int[][] frictionMapping = new int[SharedC.Map.REGION_SIZE_UNIT][SharedC.Map.REGION_SIZE_UNIT];

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

    public int[][] getFrictionMapping() {
        return frictionMapping;
    }

}
