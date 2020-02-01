package com.gadarts.war.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gadarts.war.Profiler;
import com.gadarts.war.menu.console.ConsoleEventsSubscriber;
import com.gadarts.war.menu.console.commands.BordersCommand;
import com.gadarts.war.menu.console.commands.ConsoleCommands;
import com.gadarts.war.menu.console.commands.ProfilerCommand;
import com.gadarts.war.sound.SoundPlayer;

public abstract class BaseGameScreen implements Screen, ConsoleEventsSubscriber {
	private SoundPlayer soundPlayer;

	@Override
	public void show() {

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

	protected String reactToCommand(ConsoleCommands command, Profiler profiler, Stage stage) {
		String msg = null;
		if (command == ConsoleCommands.PROFILER) {
			msg = commandProfileExecuted(profiler);
		} else if (command == ConsoleCommands.BORDERS) {
			msg = commandDrawBordersExecuted(stage);
		}
		return msg;
	}
}
