package com.gadarts.war.systems.player.input;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;

import java.util.List;

public interface InputEvent {
    boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers);
}
