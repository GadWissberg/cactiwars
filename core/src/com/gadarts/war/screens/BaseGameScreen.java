package com.gadarts.war.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gadarts.shared.console.Commands;
import com.gadarts.war.Profiler;
import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.menu.console.ConsoleEventsSubscriber;
import com.gadarts.war.menu.console.ConsoleImpl;
import com.gadarts.war.menu.console.commands.CommandsList;
import com.gadarts.war.menu.console.commands.types.BordersCommand;
import com.gadarts.war.menu.console.commands.types.ProfilerCommand;
import com.gadarts.war.menu.hud.MenuEventsSubscriber;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.sound.SoundsDefinitions;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseGameScreen implements Screen, ConsoleEventsSubscriber {
	private SoundPlayer soundPlayer;
	private List<MenuEventsSubscriber> subscribers = new ArrayList<>();
	private GameMenu menu;
	private Stage stage = new Stage();

	public void subscribeForMenuEvents(MenuEventsSubscriber subscriber) {
		if (subscribers.contains(subscriber)) return;
		subscribers.add(subscriber);
	}

	public void activateMenu() {
		menu.setVisible(true);
		getSoundPlayer().play(SoundsDefinitions.MENU_SELECT);
		for (MenuEventsSubscriber subscriber : subscribers) {
			subscriber.onMenuActivated();
		}
	}

	public void deactivateMenu() {
		menu.setVisible(false);
		getSoundPlayer().play(SoundsDefinitions.MENU_SELECT);
		for (MenuEventsSubscriber subscriber : subscribers) {
			subscriber.onMenuDeactivated();
		}
	}

	public SoundPlayer getSoundPlayer() {
		return soundPlayer;
	}

	public void setSoundPlayer(SoundPlayer soundPlayer) {
		this.soundPlayer = soundPlayer;
	}

	@Override
	public void render(float v) {

	}

	@Override
	public void resize(int i, int i1) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		ConsoleImpl consoleImpl = stage.getRoot().findActor(ConsoleImpl.NAME);
		consoleImpl.dispose();
		stage.dispose();
	}

	public abstract void onEscPressed();

	protected String commandProfileExecuted(Profiler profiler) {
		String msg;
		profiler.toggle();
		msg = profiler.isEnabled() ? ProfilerCommand.PROFILING_ACTIVATED : ProfilerCommand.PROFILING_DEACTIVATED;
		return msg;
	}

	protected String commandDrawBordersExecuted(Stage stage) {
		String msg;
		stage.setDebugAll(!stage.isDebugAll());
		msg = stage.isDebugAll() ? BordersCommand.BORDERS_ACTIVATED : BordersCommand.BORDERS_DEACTIVATED;
		return msg;
	}

	protected String reactToCommand(Commands command, Profiler profiler, Stage stage) {
		String msg = null;
		if (command == CommandsList.PROFILER) {
			msg = commandProfileExecuted(profiler);
		} else if (command == CommandsList.BORDERS) {
			msg = commandDrawBordersExecuted(stage);
		}
		return msg;
	}

	public Stage getStage() {
		return stage;
	}

	public GameMenu getMenu() {
		return menu;
	}

	public void setMenu(GameMenu menu) {
		this.menu = menu;
	}
}
