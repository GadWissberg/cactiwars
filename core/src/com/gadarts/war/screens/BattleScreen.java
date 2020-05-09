package com.gadarts.war.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.gadarts.shared.console.CommandParameter;
import com.gadarts.shared.console.Commands;
import com.gadarts.shared.console.ConsoleCommandResult;
import com.gadarts.shared.level.Map;
import com.gadarts.shared.level.MapModeler;
import com.gadarts.shared.par.SectionType;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.InGameScreen;
import com.gadarts.war.factories.ActorFactory;
import com.gadarts.war.level.MapCreator;
import com.gadarts.war.menu.console.ConsoleImpl;
import com.gadarts.war.menu.hud.Hud;
import com.gadarts.war.systems.CharacterSystem;
import com.gadarts.war.systems.EnvironmentSystem;
import com.gadarts.war.systems.GameEntitySystem;
import com.gadarts.war.systems.SystemsHandler;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystem;
import com.gadarts.war.systems.player.input.GamePlayInputHandler;
import com.gadarts.war.systems.render.RenderSystem;

/**
 * Screen of game-play.
 */
public class BattleScreen extends BaseGameScreen implements InGameScreen {
	private static boolean paused;
	private PooledEngine entitiesEngine;
	private ActorFactory actorFactory;
	private Hud hud;

	/**
	 * @return Whether game is paused.
	 */
	public static boolean isPaused() {
		return paused;
	}

	/**
	 * Pauses game and activates the menu.
	 */
	public void pauseGame() {
		BattleScreen.paused = true;
		activateMenu();
	}

	@Override
	public PooledEngine getEntitiesEngine() {
		return entitiesEngine;
	}

	@Override
	public ActorFactory getActorFactory() {
		return actorFactory;
	}

	@Override
	public void resumeGame() {
		BattleScreen.paused = false;
		deactivateMenu();
	}

	@Override
	public void show() {
		initialize();
	}

	private void initialize() {
		entitiesEngine = new PooledEngine();
		actorFactory = new ActorFactory(entitiesEngine, getSoundPlayer());
		createSystemsHandler();
		createWorld();
		initializeInput();
		createHud();
		if (DefaultGameSettings.MENU_ON_START) pauseGame();
	}

	private void createHud() {
		hud = new Hud(entitiesEngine.getSystem(RenderSystem.class), getStage());
		subscribeForMenuEvents(entitiesEngine.getSystem(CharacterSystem.class));
		subscribeForMenuEvents(entitiesEngine.getSystem(EnvironmentSystem.class));
		ConsoleImpl actor = getStage().getRoot().findActor(ConsoleImpl.NAME);
		actor.subscribeForEvents(entitiesEngine.getSystem(RenderSystem.class));
	}

	private void createSystemsHandler() {
		SystemsHandler systemsHandler = new SystemsHandler(entitiesEngine, getSoundPlayer());
		systemsHandler.init(this);
	}

	private void initializeInput() {
		if (!DefaultGameSettings.SPECTATOR) {
			InputMultiplexer multiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
			GamePlayInputHandler processor = new GamePlayInputHandler();
			multiplexer.addProcessor(processor);
			Gdx.input.setInputProcessor(multiplexer);
			processor.subscribeForInputEvents(entitiesEngine.getSystem(PlayerSystem.class));
		}
	}

	private void createWorld() {
		createLevel();
		entitiesEngine.getSystem(RenderSystem.class).onWorldInitialized();
	}

	private void createLevel() {
		MapCreator mapCreator = new MapCreator(entitiesEngine);
		String fileName = SectionType.MAP + "/" + "test";
		try {
			mapCreator.createLevelIntoEngine(GameAssetManager.getInstance().get(fileName, Map.class), actorFactory);
		} catch (MapModeler.MapModellingException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void render(float delta) {
		entitiesEngine.update(delta);
		hud.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		hud.resize(width, height);
		entitiesEngine.getSystems().forEach(system -> ((GameEntitySystem) system).onResize(width, height));
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
		entitiesEngine.getSystem(PhysicsSystem.class).dispose();
		entitiesEngine.getSystem(RenderSystem.class).dispose();
		hud.dispose();
	}

	@Override
	public void onEscPressed() {
		if (BattleScreen.paused) {
			resumeGame();
		} else {
			pauseGame();
		}
		ConsoleImpl actor = getStage().getRoot().findActor(ConsoleImpl.NAME);
		actor.deactivate();
	}

	@Override
	public void onConsoleActivated() {
		paused = true;
	}

	@Override
	public boolean onCommandRun(Commands command, ConsoleCommandResult consoleCommandResult) {
		return onCommandRun(command, consoleCommandResult, null);
	}

	@Override
	public boolean onCommandRun(Commands command, ConsoleCommandResult consoleCommandResult, CommandParameter parameter) {
		consoleCommandResult.setMessage(reactToCommand(command, hud.getProfiler(), getStage()));
		return true;
	}


	@Override
	public void onConsoleDeactivated() {
		if (getMenu().isVisible()) return;
		paused = false;
	}
}
