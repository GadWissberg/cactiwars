package com.gadarts.war.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gadarts.shared.level.Map;
import com.gadarts.shared.par.SectionType;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameSettings;
import com.gadarts.war.InGameScreen;
import com.gadarts.war.factories.ActorFactory;
import com.gadarts.war.level.MapCreator;
import com.gadarts.war.menu.Hud;
import com.gadarts.war.sound.SFX;
import com.gadarts.war.systems.CharacterSystem;
import com.gadarts.war.systems.EnvironmentSystem;
import com.gadarts.war.systems.SystemsHandler;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystem;
import com.gadarts.war.systems.player.input.GamePlayInputHandler;
import com.gadarts.war.systems.render.RenderSystem;

public class BattleScreen extends BaseGameScreen implements InGameScreen {
    private static boolean paused;
    private PooledEngine entitiesEngine;
    private ActorFactory actorFactory;
    private Hud hud;

    public static boolean isPaused() {
        return paused;
    }

    public void pauseGame() {
        BattleScreen.paused = true;
        hud.activateMenu();
    }

    @Override
    public Stage getHudStage() {
        return hud.getStage();
    }

    @Override
    public PooledEngine getEntitiesEngine() {
        return entitiesEngine;
    }

    @Override
    public void resumeGame() {
        BattleScreen.paused = false;
        hud.deactivate();
    }

    @Override
    public void show() {
        if (!GameSettings.MUTE_AMB_SOUNDS) {
            getSoundPlayer().play(GameAssetManager.getInstance().get(SFX.AMB_WIND.getFileName(), Sound.class), true);
        }
        entitiesEngine = new PooledEngine();
        createSystemsHandler();
        actorFactory = new ActorFactory(entitiesEngine, getSoundPlayer());
        createWorld();
        initializeInput();
        hud = new Hud(entitiesEngine.getSystem(RenderSystem.class), this);
        hud.subscribeForEvents(entitiesEngine.getSystem(CharacterSystem.class));
        hud.subscribeForEvents(entitiesEngine.getSystem(EnvironmentSystem.class));
        if (GameSettings.MENU_ON_START) pauseGame();
    }

    private void createSystemsHandler() {
        SystemsHandler systemsHandler = new SystemsHandler(entitiesEngine, getSoundPlayer());
        systemsHandler.init(this);
    }

    private void initializeInput() {
        if (!GameSettings.SPECTATOR) {
            InputMultiplexer multiplexer = new InputMultiplexer();
            GamePlayInputHandler processor = new GamePlayInputHandler();
            multiplexer.addProcessor(processor);
            Gdx.input.setInputProcessor(multiplexer);
            processor.subscribeForInputEvents(entitiesEngine.getSystem(PlayerSystem.class));
        }
    }

    private void createWorld() {
        createLevel();
    }

    private void createLevel() {
        MapCreator mapCreator = new MapCreator(entitiesEngine);
        String fileName = SectionType.MAP + "/" + "test";
        mapCreator.createLevelIntoEngine(GameAssetManager.getInstance().get(fileName, Map.class), actorFactory);
    }


    @Override
    public void render(float delta) {
        entitiesEngine.update(delta);
        hud.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        hud.resize(width, height);
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
        resumeGame();
    }

}
