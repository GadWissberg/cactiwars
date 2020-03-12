package com.gadarts.war.systems.player.input.definitions.down;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.war.InGameScreen;
import com.gadarts.war.components.CameraComponent;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.sound.SFX;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

import static com.gadarts.war.systems.physics.PhysicsSystem.auxVector;

public class Ctrl implements InputEvent {

    @Override
    public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers, InGameScreen parentScreen) {
        if (!ComponentsMapper.players.has(entity)) return false;
        parentScreen.getActorFactory().createBullet(ComponentsMapper.physics.get(entity).getMotionState().getWorldTranslation(auxVector));
        ComponentsMapper.physics.get(entity).getBody().applyTorqueImpulse(auxVector.set(1, 0, 0).setLength(2000));
        CameraComponent cameraComponent = ComponentsMapper.camera.get(parentScreen.getEntitiesEngine().getEntitiesFor(Family.all(CameraComponent.class).get()).first());
        parentScreen.getSoundPlayer().play(SFX.HEAVY_METAL_CRASH_1, cameraComponent.getCamera(), auxVector.set(10, 0, 0));
        return true;
    }
}
