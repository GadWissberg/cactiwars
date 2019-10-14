package com.gadarts.war.systems.player.input.definitions.down.arrows;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.character.MovementState;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;

import java.util.List;

public class ArrowUpDown extends ArrowPressedEvent {
    @Override
    public boolean run(Entity entity, List<PlayerSystemEventsSubscriber> subscribers) {
        super.run(entity, subscribers);
        ComponentsMapper.characters.get(entity).setMovementState(MovementState.ACCELERATING);
        for (PlayerSystemEventsSubscriber subscriber : subscribers) {
            subscriber.onMovementAccelerationBegan();
        }
        return true;
    }
}
