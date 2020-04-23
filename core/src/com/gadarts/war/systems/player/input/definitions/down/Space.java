package com.gadarts.war.systems.player.input.definitions.down;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.gadarts.war.InGameScreen;
import com.gadarts.war.components.CameraComponent;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.sound.SoundsDefinitions;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

public class Space implements InputEvent {
    @Override
    public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers, InGameScreen parentScreen) {
        ComponentsMapper.physics.get(entity).getBody().applyTorqueImpulse(PhysicsSystem.auxVector3_1.set(1, 0, 0).setLength(2000));
        CameraComponent cameraComponent = ComponentsMapper.camera.get(parentScreen.getEntitiesEngine().getEntitiesFor(Family.all(CameraComponent.class).get()).first());
        parentScreen.getSoundPlayer().playWithPosition(SoundsDefinitions.HEAVY_METAL_CRASH_1, cameraComponent.getCamera(), PhysicsSystem.auxVector3_1.set(10, 0, 0));
        return true;
    }
}
