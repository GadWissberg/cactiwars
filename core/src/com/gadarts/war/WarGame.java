package com.gadarts.war;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.gadarts.shared.console.CommandParameter;
import com.gadarts.shared.console.Commands;
import com.gadarts.shared.console.Console;
import com.gadarts.shared.console.ConsoleCommandResult;
import com.gadarts.shared.par.MainParLoadingFailureException;
import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.menu.console.ConsoleEventsSubscriber;
import com.gadarts.war.menu.console.ConsoleImpl;
import com.gadarts.war.menu.console.commands.CommandsList;
import com.gadarts.war.menu.console.commands.types.AudioCommand;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.sound.SoundTypes;

import java.io.IOException;

public class WarGame extends Game implements ConsoleEventsSubscriber {
	protected GameMenu menu;
	private ConsoleImpl consoleImpl;
	private final SoundPlayer soundPlayer = new SoundPlayer();

	@Override
	public void create() {
		try {
			Gdx.input.setInputProcessor(new InputMultiplexer());
			consoleImpl = new ConsoleImpl();
			consoleImpl.subscribeForEvents(this);
			BaseGameScreen gameScreen = DefaultGameSettings.INITIAL_SCREEN.getScreenClass().newInstance();
			gameScreen.getStage().addActor(consoleImpl);
			GameAssetManager.getInstance().loadAssets(consoleImpl);
			createMenu();
			setScreen(gameScreen);
		} catch (IOException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		} catch (MainParLoadingFailureException e) {
			consoleImpl.insertNewLog(e.getMessage(), true, Console.ERROR_COLOR);
			e.printStackTrace();
		}
	}

	@Override
	public void setScreen(Screen screen) {
		BaseGameScreen gameScreen = (BaseGameScreen) screen;
		initializeScreen(gameScreen);
		menu.initialize(gameScreen);
		InputMultiplexer multiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
		multiplexer.addProcessor(gameScreen.getStage());
		consoleImpl.toFront();
		super.setScreen(screen);
	}


	private void initializeScreen(BaseGameScreen screen) {
		screen.setMenu(menu);
		screen.setSoundPlayer(soundPlayer);
		consoleImpl.subscribeForEvents(screen);
	}

	private void createMenu() {
		menu = new GameMenu();
	}

	@Override
	public void dispose() {
		GameAssetManager.getInstance().dispose();
		screen.dispose();
	}

	@Override
	public void onConsoleActivated() {

	}

	@Override
	public boolean onCommandRun(Commands command, ConsoleCommandResult consoleCommandResult) {
		return false;
	}

	@Override
	public boolean onCommandRun(Commands command, ConsoleCommandResult consoleCommandResult, CommandParameter parameter) {
		boolean result = false;
		if (command == CommandsList.AUDIO) {
			result = configureSoundPlayerAccordingToCommand(parameter);
		}
		return result;
	}

	private boolean configureSoundPlayerAccordingToCommand(CommandParameter parameter) {
		boolean result = false;
		try {
			String alias = parameter.getAlias().toUpperCase();
			if (alias.equals(AudioCommand.AllParameter.ALIAS.toUpperCase())) {
				soundPlayer.setEnabled(parameter.getParameterValue());
			} else {
				soundPlayer.setEnabled(SoundTypes.valueOf(alias), parameter.getParameterValue());
			}
			result = true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void onConsoleDeactivated() {

	}
}
