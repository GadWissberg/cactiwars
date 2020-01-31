package com.gadarts.war.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gadarts.war.GameSettings;
import com.gadarts.war.Profiler;
import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.systems.render.RenderSystem;

import java.io.File;

public class MainMenuScreen extends BaseGameScreen {

	private final Stage stage = new Stage();
	private FlyingCacti flyingCacti = new FlyingCacti();
	private Profiler profiler;
	private Texture background;
	private ShaderProgram backgroundShaderProgram;
	private MainMenuBackground backgroundObject;

	@Override
	public void show() {
		super.show();
		flyingCacti.initialize();
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		GameMenu menu = new GameMenu(this);
		menu.initialize(stage);
		menu.setVisible(true);
		stage.setDebugAll(GameSettings.DRAW_TABLES_BORDERS);
		stage.setViewport(new ScreenViewport(stage.getCamera()));
		profiler = new Profiler(stage);
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(1f, 0, 0, 1);
		pixmap.fillRectangle(0, 0, width, height);
		background = new Texture(pixmap);
		pixmap.dispose();
		String vertexShader = Gdx.files.internal("shaders" + File.separator + "vertex_main_menu_background.glsl").readString();
		ShaderProgram.pedantic = false;
		String fragmentShader = Gdx.files.internal("shaders" + File.separator + "fragment_main_menu_background.glsl").readString();
		backgroundShaderProgram = new ShaderProgram(vertexShader, fragmentShader);
		backgroundObject = new MainMenuBackground(background, backgroundShaderProgram);
	}

	@Override
	public void dispose() {
		super.dispose();
		stage.dispose();
		flyingCacti.dispose();
		background.dispose();
		backgroundShaderProgram.dispose();
	}

	@Override
	public void render(float deltaTime) {
		super.render(deltaTime);
		flyingCacti.update();
		RenderSystem.resetDisplay(Color.BLACK);
		backgroundObject.draw(stage.getBatch(), 1f);
		flyingCacti.render(deltaTime);
		stage.act(deltaTime);
		stage.draw();
		profiler.update();
		profiler.reset();
	}

	@Override
	public void onEscPressed() {

	}


	@Override
	public void consoleActivated() {

	}

	@Override
	public void consoleDeactivated() {

	}
}
