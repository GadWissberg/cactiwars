package com.gadarts.war.systems.physics;

import com.badlogic.ashley.core.Entity;

public interface GameContactListenerEventsSubscriber {
    void onStaticEnvironmentObjectHardCollision(Entity entity);
}
