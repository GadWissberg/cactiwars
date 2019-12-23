package com.gadarts.war.systems.player.input.definitions.down.arrows;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.GameScreen;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

public abstract class ArrowPressedEvent implements InputEvent {
    @Override
    public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers, GameScreen parentScreen) {
        return true;
    }
}
