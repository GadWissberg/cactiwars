package com.gadarts.war;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gadarts.war.systems.render.RenderSystem;

public class Hud {
    private final Stage stage;
    private Profiler profiler;

    public Hud(RenderSystem renderSystem) {
        stage = new Stage();
        ScreenViewport viewport = new ScreenViewport(stage.getCamera());
        stage.setViewport(viewport);
        profiler = new Profiler(stage, renderSystem);
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
}
