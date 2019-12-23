package com.gadarts.war.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gadarts.war.*;
import com.gadarts.war.menu.input.MenuInputHandler;
import com.gadarts.war.systems.render.RenderSystem;

public class Hud {
    private final Stage stage;
    private Profiler profiler;
    private GameMenu menu;

    public Hud(RenderSystem renderSystem, GameScreen parentScreen) {
        stage = new Stage();
        stage.setDebugAll(GameSettings.DRAW_TABLES_BORDERS);
        createMenu(parentScreen);
        stage.setViewport(new ScreenViewport(stage.getCamera()));
        profiler = new Profiler(stage, renderSystem);
    }

    private void createMenu(GameScreen parentScreen) {
        BitmapFont font = GameAssetManager.getInstance().get("cactus_med.ttf", BitmapFont.class);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        stage.addActor(new Label(GameC.General.GAME + " v" + GameC.General.VERSION, labelStyle));
        createMenuTable(parentScreen);
        addMenuOptions(labelStyle);
        menu.update();
        menu.setVisible(BattleScreen.isPaused());
    }

    private void createMenuTable(GameScreen parentScreen) {
        MenuInputHandler menuInputHandler = new MenuInputHandler();
        menu = new GameMenu(menuInputHandler, parentScreen);
        InputMultiplexer multiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
        multiplexer.addProcessor(menuInputHandler);
        Gdx.input.setInputProcessor(multiplexer);
        stage.addActor(menu);
    }

    private void addMenuOptions(Label.LabelStyle labelStyle) {
        GameMenuOptions[] options = GameMenuOptions.values();
        for (GameMenuOptions option : options) {
            GameMenuOption menuOption = new GameMenuOption(option, labelStyle);
            menuOption.setName(option.name());
            menu.addOption(menuOption);
        }
    }


    public void render(float delta) {
        profiler.update();
        stage.act(delta);
        stage.draw();
        profiler.reset();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void activateMenu() {
        menu.setVisible(true);
    }

    public void deactivate() {
        menu.setVisible(false);
    }
}
