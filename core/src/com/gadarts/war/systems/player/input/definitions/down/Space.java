package com.gadarts.war.systems.player.input.definitions.down;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

public class Space implements InputEvent {
    @Override
    public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers) {
        ComponentsMapper.physics.get(entity).getBody().applyTorqueImpulse(PhysicsSystem.auxVector.set(1,0,0).setLength(2000));
        return true;
    }
}
