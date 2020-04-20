package com.gadarts.war.menu.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.Profiler;
import com.gadarts.war.screens.BattleScreen;
import com.gadarts.war.systems.render.RenderSystem;

/**
 * Represents the UI in-game.
 */
public class Hud implements Disposable {
	private final RenderSystem renderSystem;
	private final Stage stage;
	private final Profiler profiler;
	private final BlurEffectHandler blurEffectData;
	private final ShaderProgram regularShaderProgram;

	public Hud(RenderSystem renderSystem, Stage stage) {
		this.stage = stage;
		this.renderSystem = renderSystem;
		stage.setDebugAll(DefaultGameSettings.DRAW_TABLES_BORDERS);
		stage.setViewport(new ScreenViewport(stage.getCamera()));
		profiler = new Profiler(stage, renderSystem);
		regularShaderProgram = stage.getBatch().getShader();
		blurEffectData = new BlurEffectHandler();
	}

	private void applyBlur() {
		Batch batch = stage.getBatch();
		batch.setShader(blurEffectData.getShaderProgram());
		batch.getProjectionMatrix().idt();
		batch.begin();
		blurEffectData.refreshBlurUniforms();
		batch.draw(blurEffectData.getFrameBuffer().getColorBufferTexture(), -1, 1, 2, -2);
		batch.end();
	}

	/**
	 * Renders the UI and blur-effect.
	 * Also updates the profiler if enabled.
	 *
	 * @param delta The time passed since last frame.
	 */
	public void render(float delta) {
		if (BattleScreen.isPaused()) {
			initializeBlurEffect();
		}
		profiler.update();
		stage.act(delta);
		stage.draw();
		profiler.reset();
	}

	private void initializeBlurEffect() {
		renderSystem.render(Gdx.graphics.getDeltaTime(), blurEffectData.getFrameBuffer());
		applyBlur();
		stage.getBatch().setShader(regularShaderProgram);
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}

	public Profiler getProfiler() {
		return profiler;
	}

	@Override
	public void dispose() {
		blurEffectData.dispose();
		stage.dispose();
	}
}
