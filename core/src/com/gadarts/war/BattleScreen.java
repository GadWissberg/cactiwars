package com.gadarts.war;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.shared.level.Map;
import com.gadarts.shared.par.SectionType;
import com.gadarts.war.factories.CharacterFactory;
import com.gadarts.war.level.LevelCreator;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.systems.RenderSystem;
import com.gadarts.war.systems.SystemsHandler;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystem;
import com.gadarts.war.systems.player.input.InputHandler;

class BattleScreen implements Screen {
    private PooledEngine entitiesEngine;
    private SystemsHandler systemsHandler;
    private CharacterFactory characterFactory;
    private Hud hud;
    private SoundPlayer soundPlayer;
    private Vector3 auxVector = new Vector3();

    @Override
    public void show() {
        soundPlayer = new SoundPlayer();
        entitiesEngine = new PooledEngine();
        createSystemsHandler();
        characterFactory = new CharacterFactory(entitiesEngine, soundPlayer);
        createWorld();
        initializeInput();
        hud = new Hud(entitiesEngine.getSystem(RenderSystem.class));
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
//        createPlayer();
//        createStreetLamp();
//        createRocks();
    }

//    private void createRocks() {
//        String modelFileName = GameC.Files.MODELS_FOLDER_NAME + "/" + "rock_1.g3dj";
//        Entity rock = characterFactory.createEnvironmentObject(modelFileName, auxVector.set(4, 0, 1), true, actor.getRotation());
//        entitiesEngine.addEntity(rock);
//        modelFileName = GameC.Files.MODELS_FOLDER_NAME + "/" + "rock_2.g3dj";
//        rock = characterFactory.createEnvironmentObject(modelFileName, auxVector.set(4, 0, 3), true, actor.getRotation());
//        entitiesEngine.addEntity(rock);
//        modelFileName = GameC.Files.MODELS_FOLDER_NAME + "/" + "rock_3.g3dj";
//        rock = characterFactory.createEnvironmentObject(modelFileName, auxVector.set(3, 0, 5), true, actor.getRotation());
//        entitiesEngine.addEntity(rock);
//    }

    private void createStreetLamp() {
    }

    private void createLevel() {
        LevelCreator levelCreator = new LevelCreator(entitiesEngine);
        String fileName = SectionType.MAP + "/" + "test";
        levelCreator.createLevelIntoEngine(GameAssetManager.getInstance().get(fileName, Map.class), characterFactory);
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
