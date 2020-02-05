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
import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.menu.console.ConsoleImpl;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.screens.BattleScreen;
import com.gadarts.war.sound.SFX;
import com.gadarts.war.systems.render.RenderSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Hud {
	private final Stage stage;
	private final RenderSystem renderSystem;
	private final BaseGameScreen parentScreen;
	private final ConsoleImpl consoleImpl;
	private ShaderProgram blurShaderProgram;
	private Profiler profiler;
	private GameMenu menu;
	private FrameBuffer blurFrameBuffer;
	private int blurRadiusLocation;
	private int blurDirectionLocation;
	private int blurResolutionLocation;
	private Vector2 blurMag = new Vector2(1, 1);
	private ShaderProgram regularShaderProgram;
	private List<HudEventsSubscriber> subscribers = new ArrayList<>();

	public Hud(RenderSystem renderSystem, BaseGameScreen parentScreen) {
		this.renderSystem = renderSystem;
		this.parentScreen = parentScreen;
		stage = new Stage();
		stage.setDebugAll(DefaultGameSettings.DRAW_TABLES_BORDERS);
		menu = new GameMenu(parentScreen);
		menu.initialize(stage);
		stage.setViewport(new ScreenViewport(stage.getCamera()));
		profiler = new Profiler(stage, renderSystem);
		initializeBlur();
		consoleImpl = new ConsoleImpl();
		consoleImpl.subscribeForEvents(parentScreen);
		stage.addActor(consoleImpl);
	}

	public ConsoleImpl getConsoleImpl() {
		return consoleImpl;
	}

	public void dispose() {
		blurShaderProgram.dispose();
		blurFrameBuffer.dispose();
		ConsoleImpl consoleImpl = getStage().getRoot().findActor(ConsoleImpl.NAME);
		consoleImpl.dispose();
		stage.dispose();
	}

	private void initializeBlur() {
		regularShaderProgram = stage.getBatch().getShader();
		String blurFragmentShader = Gdx.files.internal("shaders" + File.separator + "fragment_blur.glsl").readString();
		String blurVertexShader = Gdx.files.internal("shaders" + File.separator + "vertex_blur.glsl").readString();
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

	public void activateMenu() {
		menu.setVisible(true);
		parentScreen.getSoundPlayer().play(SFX.MENU_SELECT);
		for (HudEventsSubscriber subscriber : subscribers) {
			subscriber.onMenuActivated();
		}
	}

	public void deactivate() {
		menu.setVisible(false);
		parentScreen.getSoundPlayer().play(SFX.MENU_SELECT);
		for (HudEventsSubscriber subscriber : subscribers) {
			subscriber.onMenuDeactivated();
		}
	}

	public void subscribeForEvents(HudEventsSubscriber subscriber) {
		if (subscribers.contains(subscriber)) return;
		subscribers.add(subscriber);
	}

	public Stage getStage() {
		return stage;
	}

	public Profiler getProfiler() {
		return profiler;
	}
}
