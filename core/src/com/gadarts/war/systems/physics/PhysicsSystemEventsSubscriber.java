package com.gadarts.war.systems.physics;

import com.badlogic.ashley.core.Entity;

public interface PhysicsSystemEventsSubscriber {

    void collisionShapesDrawingInitialized(CollisionShapesDebugDrawing collisionShapesDebugDrawingMethod);

    void onEnvironmentObjectStaticValueChange(boolean b, Entity entity);
}
