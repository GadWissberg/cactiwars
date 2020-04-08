package com.gadarts.war;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.gadarts.shared.console.Console;
import com.gadarts.shared.par.MainParLoadingFailureException;
import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.menu.console.ConsoleImpl;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.sound.SoundPlayer;

import java.io.IOException;

public class WarGame extends Game {
	protected GameMenu menu;
	private ConsoleImpl consoleImpl;
	private SoundPlayer soundPlayer = new SoundPlayer();

	@Override
	public void create() {
		try {
			InputMultiplexer processor = new InputMultiplexer();
			Gdx.input.setInputProcessor(processor);
			consoleImpl = new ConsoleImpl();
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
}
