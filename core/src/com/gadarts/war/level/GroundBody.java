package com.gadarts.war.level;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class GroundBody {
    private btRigidBody.btRigidBodyConstructionInfo bodyInfo;
    private btCollisionObject body;

    public void setBodyInfo(btRigidBody.btRigidBodyConstructionInfo bodyInfo) {
        this.bodyInfo = bodyInfo;
    }

    public btRigidBody.btRigidBodyConstructionInfo getBodyInfo() {
        return bodyInfo;
    }

    public void setBody(btRigidBody body) {
        this.body = body;
    }

    public btCollisionObject getBody() {
        return body;
    }

}
