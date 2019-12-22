package com.gadarts.war.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gadarts.war.*;
import com.gadarts.war.systems.render.RenderSystem;

public class Hud {
    private final Stage stage;
    private Profiler profiler;
    private Table menuTable;

    public Hud(RenderSystem renderSystem) {
        stage = new Stage();
        stage.setDebugAll(GameSettings.DRAW_TABLES_BORDERS);
        BitmapFont font = GameAssetManager.getInstance().get("cactus_med.ttf", BitmapFont.class);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        stage.addActor(new Label("Cactillery v" + GameC.VERSION, labelStyle));
        createMenu(labelStyle);
        stage.setViewport(new ScreenViewport(stage.getCamera()));
        profiler = new Profiler(stage, renderSystem);
    }

    private void createMenu(Label.LabelStyle labelStyle) {
        menuTable = new Table();
        GameMenuOption[] options = GameMenuOption.values();
        for (GameMenuOption option : options) {
            menuTable.add(new Label(option.getLabel(), labelStyle)).row();
        }
        menuTable.setFillParent(true);
        stage.addActor(menuTable);
        menuTable.setVisible(BattleScreen.isPaused());
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
//        menuTable
    }
}
