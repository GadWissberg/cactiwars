package com.gadarts.war.systems.render;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.gadarts.war.systems.physics.CollisionShapesDebugDrawing;

public class RenderingDebugHandler {
    private int numberOfVisible;
    private CollisionShapesDebugDrawing collisionShapesDebugDrawingMethod;

    public int getNumberOfVisible() {
        return numberOfVisible;
    }

    public CollisionShapesDebugDrawing getCollisionShapesDebugDrawingMethod() {
        return collisionShapesDebugDrawingMethod;
    }

    public void resetCounter() {
        numberOfVisible = 0;
    }

    public void renderCollisionShapes(PerspectiveCamera camera) {
        if (collisionShapesDebugDrawingMethod != null) collisionShapesDebugDrawingMethod.drawCollisionShapes(camera);
    }

    public void inc() {
        numberOfVisible++;
    }

    public void setCollisionShapesDrawing(CollisionShapesDebugDrawing collisionShapesDebugDrawingMethod) {
        this.collisionShapesDebugDrawingMethod = collisionShapesDebugDrawingMethod;
    }
}
