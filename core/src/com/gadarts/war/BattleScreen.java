package com.gadarts.war;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.gadarts.shared.level.Map;
import com.gadarts.war.factories.CharacterFactory;
import com.gadarts.war.level.LevelCreator;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.systems.CameraSystem;
import com.gadarts.war.systems.RenderSystem;
import com.gadarts.war.systems.SystemsHandler;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystem;
import com.gadarts.war.systems.player.input.InputHandler;

class BattleScreen implements Screen {
    private PooledEngine entitiesEngine;
    private SystemsHandler systemsHandler;
    private CharacterFactory characterFactory;
    private Hud ui;
    private SoundPlayer soundPlayer;

    @Override
    public void show() {
        soundPlayer = new SoundPlayer();
        entitiesEngine = new PooledEngine();
        createSystemsHandler();
        characterFactory = new CharacterFactory(entitiesEngine, soundPlayer);
        createWorld();
        initializeInput();
        ui = new Hud(entitiesEngine.getSystem(RenderSystem.class));
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
        createPlayer();
        createStreetLamp();
    }

    private void createStreetLamp() {
        String modelFileName = GameC.Files.MODELS_FOLDER_NAME + "/" + "street_lamp.g3dj";
        Entity lamp = characterFactory.createEnvironmentObject(modelFileName, 1, 2, 1);
        entitiesEngine.addEntity(lamp);
    }

    private void createPlayer() {
        String modelFileName = GameC.Files.MODELS_FOLDER_NAME + "/" + "artillery.g3dj";
        Entity player = characterFactory.createPlayer(modelFileName, 2, 10, 4);
        entitiesEngine.addEntity(player);
        entitiesEngine.getSystem(CameraSystem.class).lockToTarget(player);
        entitiesEngine.getSystem(PlayerSystem.class).setPlayer(player);
    }

    private void createLevel() {
        LevelCreator levelCreator = new LevelCreator();
        levelCreator.createLevelIntoEngine(entitiesEngine, GameAssetManager.getInstance().get("test", Map.class));
    }


    @Override
    public void render(float delta) {
        entitiesEngine.update(delta);
        ui.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        ui.resize(width, height);
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
