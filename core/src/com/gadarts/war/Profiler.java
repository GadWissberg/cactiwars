package com.gadarts.war;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.StringBuilder;
import com.gadarts.war.systems.RenderSystem;

public class Profiler {

    private final Stage stage;
    private final RenderSystem renderSystem;
    private GLProfiler glProfiler;
    private StringBuilder stringBuilder;
    private Label label;

    public Profiler(Stage stage, RenderSystem renderSystem) {
        this.stage = stage;
        this.renderSystem = renderSystem;
        stringBuilder = new StringBuilder();
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        label = new Label(stringBuilder, style);
        label.setPosition(0, Gdx.graphics.getHeight() - 90);
        stage.addActor(label);
        setGlProfiler();
    }

    private void setGlProfiler() {
        if (Gdx.app.getLogLevel() == Application.LOG_DEBUG && GameSettings.SHOW_GL_PROFILING) {
            glProfiler = new GLProfiler(Gdx.graphics);
            glProfiler.enable();
        }
    }

    public void update() {
        if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
            stringBuilder.setLength(0);
            displayLine(GameC.Profiler.FPS_STRING, Gdx.graphics.getFramesPerSecond());
            displayGlProfiling();
            displayBatchCalls();
            label.setText(stringBuilder);
        }
    }

    private void displayBatchCalls() {
        displayLine(GameC.Profiler.BATCH_UI_CALLS_STRING, ((SpriteBatch) stage.getBatch()).renderCalls);
    }

    private void displayGlProfiling() {
        displayLine(GameC.Profiler.GL_CALL_STRING, glProfiler.getCalls());
        displayLine(GameC.Profiler.GL_DRAW_CALL_STRING, glProfiler.getDrawCalls());
        displayLine(GameC.Profiler.GL_SHADER_SWITCHES_STRING, glProfiler.getShaderSwitches());
        displayLine(GameC.Profiler.GL_TEXTURE_BINDINGS_STRING, glProfiler.getTextureBindings());
        displayLine(GameC.Profiler.GL_VERTEX_COUNT_STRING, glProfiler.getVertexCount().total);
        displayLine(GameC.Profiler.VISIBLE_OBJECTS_STRING, renderSystem.getNumberOfVisible());
        glProfiler.reset();
    }

    private void displayLine(String label, Object value) {
        stringBuilder.append(label);
        stringBuilder.append(value);
        stringBuilder.append('\n');
    }

}
