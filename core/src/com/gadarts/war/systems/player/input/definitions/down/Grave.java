package com.gadarts.war.systems.player.input.definitions.down;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.InGameScreen;
import com.gadarts.war.menu.console.ConsoleImpl;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

public class Grave implements InputEvent {
	@Override
	public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers, InGameScreen parentScreen) {
		ConsoleImpl consoleImpl = parentScreen.getHudStage().getRoot().findActor(ConsoleImpl.NAME);
		if (!consoleImpl.isActive()) {
			consoleImpl.activate();
		} else {
			consoleImpl.deactivate();
		}
		return true;
	}
}
