package com.gadarts.war.systems.player.input.definitions;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.InGameScreen;
import com.gadarts.war.menu.Console;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

public class LetterQ implements InputEvent {
	@Override
	public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers, InGameScreen parentScreen) {
		Console console = parentScreen.getHudStage().getRoot().findActor(Console.NAME);
		if (!console.isActive()) {
			console.activate();
		} else {
			console.deactivate();
		}
		return true;
	}
}
