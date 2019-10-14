package com.gadarts.war.systems.player.input.definitions.up;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.character.CharacterComponent;
import com.gadarts.war.components.character.MovementState;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

public class ArrowUpUp implements InputEvent {
    @Override
    public boolean run(Entity entity, List<PlayerSystemEventsSubscriber> subscribers) {
        CharacterComponent characterComponent = ComponentsMapper.characters.get(entity);
        if (characterComponent.getMovementState() == MovementState.ACCELERATING) {
            characterComponent.setMovementState(MovementState.IDLE);
        }
        for (PlayerSystemEventsSubscriber subscriber : subscribers) {
            subscriber.onMovementAccelerationStopped();
        }
        return true;
    }
}
