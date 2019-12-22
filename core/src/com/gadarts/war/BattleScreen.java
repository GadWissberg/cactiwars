package com.gadarts.war;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.gadarts.shared.level.Map;
import com.gadarts.shared.par.SectionType;
import com.gadarts.war.factories.ActorFactory;
import com.gadarts.war.level.MapCreator;
import com.gadarts.war.menu.Hud;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.systems.SystemsHandler;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystem;
import com.gadarts.war.systems.player.input.InputHandler;
import com.gadarts.war.systems.render.RenderSystem;

public class BattleScreen implements Screen {
    private static boolean paused;
    private PooledEngine entitiesEngine;
    private SystemsHandler systemsHandler;
    private ActorFactory actorFactory;
    private Hud hud;
    private SoundPlayer soundPlayer;

    public static boolean isPaused() {
        return paused;
    }

    public void pauseGame() {
        BattleScreen.paused = true;
        hud.activateMenu();
    }

    public void resumeGame() {
        BattleScreen.paused = false;
    }

    @Override
    public void show() {
        soundPlayer = new SoundPlayer();
        entitiesEngine = new PooledEngine();
        createSystemsHandler();
        actorFactory = new ActorFactory(entitiesEngine, soundPlayer);
        createWorld();
        initializeInput();
        hud = new Hud(entitiesEngine.getSystem(RenderSystem.class));
        if (GameSettings.MENU_ON_START) paused = true;
    }

    private void createSystemsHandler() {
        systemsHandler = new SystemsHandler(entitiesEngine, soundPlayer);
        systemsHandler.init();
    }

    private void initializeInput() {
        if (!GameSettings.SPECTATOR) {
            InputHandler processor = new InputHandler();
            Gdx.input.setInputProcessor(processor);
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
    }

    public PooledEngine getEntitiesEngine() {
        return entitiesEngine;
    }

}
