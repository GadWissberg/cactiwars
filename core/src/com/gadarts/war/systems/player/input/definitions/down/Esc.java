package com.gadarts.war.systems.player.input.definitions.down;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.BattleScreen;
import com.gadarts.war.GameScreen;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

public class Esc implements InputEvent {
    @Override
    public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers, GameScreen parentScreen) {
        if (BattleScreen.isPaused()) {
            parentScreen.resumeGame();
        } else {
            parentScreen.pauseGame();
        }
        return true;
    }
}
