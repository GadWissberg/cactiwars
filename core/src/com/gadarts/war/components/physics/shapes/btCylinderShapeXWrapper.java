package com.gadarts.war.components.physics.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShapeX;

public class btCylinderShapeXWrapper extends btCylinderShapeX {
    private static final Vector3 dimensions = new Vector3();

    public btCylinderShapeXWrapper() {
        super(dimensions.set(1, 1, 1));
    }

}
