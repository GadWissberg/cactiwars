package com.gadarts.war.systems.player.input.definitions.down.arrows;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.InGameScreen;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

/**
 * A procedure to be executed when an arrow is pressed.
 */
public abstract class ArrowPressedEvent implements InputEvent {

    /**
     * Called when the arrow is pressed.
     *
     * @param player       The entity which represents the player.
     * @param subscribers  A list of listeners that listen to various events of the player system.
     * @param parentScreen The current in-game screen object.
     * @return Whether the procedure has been executed successfully or not. True on default.
     */
    @Override
    public boolean execute(Entity player, List<PlayerSystemEventsSubscriber> subscribers, InGameScreen parentScreen) {
        return true;
    }
}
