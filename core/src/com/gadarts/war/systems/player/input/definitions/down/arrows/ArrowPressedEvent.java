package com.gadarts.war.systems.player.input.definitions.down.arrows;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

public abstract class ArrowPressedEvent implements InputEvent {
    @Override
    public boolean run(Entity entity, List<PlayerSystemEventsSubscriber> subscribers) {
        btRigidBody body = ComponentsMapper.physics.get(entity).getBody();
        if (!body.isActive()) {
            body.activate();
        }
        return true;
    }
}
