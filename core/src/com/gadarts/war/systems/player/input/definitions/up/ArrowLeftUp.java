package com.gadarts.war.systems.player.input.definitions.up;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.GameScreen;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

public class ArrowLeftUp implements InputEvent {
    @Override
    public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers, GameScreen parentScreen) {
        ComponentsMapper.characters.get(entity).setRotation(0);
        return true;
    }
}
