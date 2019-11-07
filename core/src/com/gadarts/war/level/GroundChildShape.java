package com.gadarts.war.level;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;

public class GroundChildShape extends btBoxShape {
    private final int test;

    public GroundChildShape(Vector3 halfExtents, int i) {
        super(halfExtents);
        this.test = i;
    }

    public int getTest() {
        return test;
    }
}
