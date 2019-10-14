package com.gadarts.war.components.physics.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;

public class btCylinderShapeWrapper extends btCylinderShape {
    private static final Vector3 dimensions = new Vector3();

    public btCylinderShapeWrapper() {
        super(dimensions.set(1, 1, 1));
    }

}
