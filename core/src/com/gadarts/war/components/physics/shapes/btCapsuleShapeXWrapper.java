package com.gadarts.war.components.physics.shapes;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShapeX;

public class btCapsuleShapeXWrapper extends btCapsuleShapeX {
    private static final Vector3 dimensions = new Vector3();

    public btCapsuleShapeXWrapper() {
        super(1,1);
    }

}
