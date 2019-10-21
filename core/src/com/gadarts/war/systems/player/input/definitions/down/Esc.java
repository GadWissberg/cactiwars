package com.gadarts.war.systems.player.input.definitions.down;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

public class Esc implements InputEvent {
    @Override
    public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers) {
        Gdx.app.exit();
        return true;
    }
}
