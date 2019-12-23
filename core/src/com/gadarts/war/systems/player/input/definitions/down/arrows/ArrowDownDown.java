package com.gadarts.war.systems.player.input.definitions.down.arrows;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.GameScreen;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.character.MovementState;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;

import java.util.List;

public class ArrowDownDown extends ArrowPressedEvent {
    @Override
    public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers, GameScreen parentScreen) {
        super.execute(entity, subscribers, parentScreen);
        ComponentsMapper.characters.get(entity).setMovementState(MovementState.REVERSE);
        return true;
    }
}
