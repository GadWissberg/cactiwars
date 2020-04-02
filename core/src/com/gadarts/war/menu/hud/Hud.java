package com.gadarts.war.menu.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.GameC;
import com.gadarts.war.Profiler;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.screens.BattleScreen;
import com.gadarts.war.systems.render.RenderSystem;

import java.io.File;

public class Hud {
	private final RenderSystem renderSystem;
	private final BaseGameScreen parentScreen;
	private final Stage stage;
	private ShaderProgram blurShaderProgram;
	private Profiler profiler;
	private FrameBuffer blurFrameBuffer;
	private int blurRadiusLocation;
	private int blurDirectionLocation;
	private int blurResolutionLocation;
	private Vector2 blurMag = new Vector2(1, 1);
	private ShaderProgram regularShaderProgram;

	public Hud(RenderSystem renderSystem, BaseGameScreen parentScreen, Stage stage) {
		this.stage = stage;
		this.renderSystem = renderSystem;
		this.parentScreen = parentScreen;
		stage = new Stage();
		stage.setDebugAll(DefaultGameSettings.DRAW_TABLES_BORDERS);
		stage.setViewport(new ScreenViewport(stage.getCamera()));
		profiler = new Profiler(stage, renderSystem);
		initializeBlur();
	}


	public void dispose() {
		blurShaderProgram.dispose();
		blurFrameBuffer.dispose();
		stage.dispose();
	}

	private void initializeBlur() {
		regularShaderProgram = stage.getBatch().getShader();
		String blurFragmentShader = Gdx.files.internal(GameC.Files.ASSETS_PATH + "shaders" + File.separator + "fragment_blur.glsl").readString();
		String blurVertexShader = Gdx.files.internal(GameC.Files.ASSETS_PATH + "shaders" + File.separator + "vertex_blur.glsl").readString();
		blurShaderProgram = new ShaderProgram(blurVertexShader, blurFragmentShader);
		blurFrameBuffer = new FrameBuffer(Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		blurRadiusLocation = blurShaderProgram.getUniformLocation(GameC.Menu.BlurShaderKeys.BLUR_RADIUS);
		blurDirectionLocation = blurShaderProgram.getUniformLocation(GameC.Menu.BlurShaderKeys.BLUR_DIRECTION);
		blurResolutionLocation = blurShaderProgram.getUniformLocation(GameC.Menu.BlurShaderKeys.BLUR_RESOLUTION);
	}

	private void applyBlur() {
		Batch batch = stage.getBatch();
		batch.setShader(blurShaderProgram);
		batch.getProjectionMatrix().idt();
		batch.begin();
		setBlurUniforms(blurMag.set(35f, 0f));
		batch.draw(blurFrameBuffer.getColorBufferTexture(), -1, 1, 2, -2);
		batch.end();
	}

	private void setBlurUniforms(Vector2 blur) {
		blurShaderProgram.setUniformf(blurRadiusLocation, blurMag.len2());
		blur.nor();
		blurShaderProgram.setUniformf(blurDirectionLocation, blurMag.x, blurMag.y);
		blurShaderProgram.setUniformf(blurResolutionLocation, ((float) Gdx.graphics.getWidth()));
	}

	public void render(float delta) {
		if (BattleScreen.isPaused()) {
			renderSystem.render(Gdx.graphics.getDeltaTime(), blurFrameBuffer);
			applyBlur();
			stage.getBatch().setShader(regularShaderProgram);
		}
		profiler.update();
		stage.act(delta);
		stage.draw();
		profiler.reset();
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}

	public Profiler getProfiler() {
		return profiler;
	}
}
