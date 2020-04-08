package com.gadarts.war;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.gadarts.shared.par.MainParLoadingFailureException;
import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.menu.console.ConsoleImpl;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.sound.SoundPlayer;

import java.io.IOException;

public class WarGame extends Game {
	protected GameMenu menu;
	private BaseGameScreen screen;
	private ConsoleImpl consoleImpl;
	private SoundPlayer soundPlayer = new SoundPlayer();

	@Override
	public void create() {
		try {
			GameAssetManager.getInstance().loadAssets();
			InputMultiplexer processor = new InputMultiplexer();
			Gdx.input.setInputProcessor(processor);
			createMenu();
			consoleImpl = new ConsoleImpl();
			createScreen(processor);
		} catch (IOException | MainParLoadingFailureException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
	}

	private void createScreen(InputMultiplexer processor) throws InstantiationException, IllegalAccessException {
		screen = DefaultGameSettings.INITIAL_SCREEN.getScreenClass().newInstance();
		initializeScreen();
		menu.initialize(screen);
		processor.addProcessor(screen.getStage());
		consoleImpl.toFront();
		setScreen(screen);
	}

	private void initializeScreen() {
		screen.setMenu(menu);
		screen.setSoundPlayer(soundPlayer);
		screen.getStage().addActor(consoleImpl);
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
