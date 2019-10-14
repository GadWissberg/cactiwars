package com.gadarts.war.components.physics.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;

public class btBoxShapeWrapper extends btBoxShape {
    private static final Vector3 dimensions = new Vector3();

    public btBoxShapeWrapper() {
        super(dimensions.set(1, 1, 1));
    }
}
