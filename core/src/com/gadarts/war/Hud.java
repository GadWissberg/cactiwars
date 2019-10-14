package com.gadarts.war;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gadarts.shared.SharedC.Resolution;
import com.gadarts.war.systems.RenderSystem;

public class Hud {
    private final Stage stage;
    private Profiler profiler;

    public Hud(RenderSystem renderSystem) {
        OrthographicCamera camera = new OrthographicCamera(Resolution.UI_WORLD_WIDTH, Resolution.UI_WORLD_HEIGHT);
        stage = new Stage(new StretchViewport(Resolution.UI_WORLD_WIDTH, Resolution.UI_WORLD_HEIGHT, camera));
        profiler = new Profiler(stage, renderSystem);
        camera.position.set(Resolution.UI_WORLD_WIDTH / 2f, Resolution.UI_WORLD_HEIGHT / 2f, 0);
    }

    public void render(float delta) {
        profiler.update();
        stage.act(delta);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
        stage.getCamera().update();
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }
}
