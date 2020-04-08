package com.gadarts.war.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gadarts.shared.console.CommandParameter;
import com.gadarts.shared.console.Commands;
import com.gadarts.shared.console.ConsoleCommandResult;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.GameC.Files;
import com.gadarts.war.Profiler;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.systems.render.RenderSystem;

import java.io.File;

public class MainMenuScreen extends BaseGameScreen {

	private FlyingCactiRenderer flyingCactiRenderer = new FlyingCactiRenderer();
	private Profiler profiler;
	private Texture background;
	private ShaderProgram backgroundShaderProgram;
	private MainMenuBackground backgroundObject;

	@Override
	public void resize(int i, int i1) {
		super.resize(i, i1);
		flyingCactiRenderer.onResize(i, i1);
	}

	@Override
	public void show() {
		super.show();
		activateMenu();
		flyingCactiRenderer.initialize();
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		stage.setDebugAll(DefaultGameSettings.DRAW_TABLES_BORDERS);
		stage.setViewport(new ScreenViewport(stage.getCamera()));
		profiler = new Profiler(stage);
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(1f, 0, 0, 1);
		pixmap.fillRectangle(0, 0, width, height);
		background = new Texture(pixmap);
		pixmap.dispose();
		String vertexShader = Gdx.files.internal(Files.ASSETS_PATH + "shaders" + File.separator + "vertex_main_menu_background.glsl").readString();
		ShaderProgram.pedantic = false;
		String fragmentShader = Gdx.files.internal(Files.ASSETS_PATH + "shaders" + File.separator + "fragment_main_menu_background.glsl").readString();
		backgroundShaderProgram = new ShaderProgram(vertexShader, fragmentShader);
		backgroundObject = new MainMenuBackground(background, backgroundShaderProgram);
	}

	@Override
	public void dispose() {
		super.dispose();
		stage.dispose();
		flyingCactiRenderer.dispose();
		background.dispose();
		backgroundShaderProgram.dispose();
	}

	@Override
	public void render(float deltaTime) {
		super.render(deltaTime);
		flyingCactiRenderer.update();
		RenderSystem.resetDisplay(Color.BLACK);
		backgroundObject.draw(stage.getBatch());
		flyingCactiRenderer.render(deltaTime);
		stage.act(deltaTime);
		stage.draw();
		profiler.update();
		profiler.reset();
	}

	@Override
	public void onEscPressed() {

	}


	@Override
	public void onConsoleActivated() {

	}

	@Override
	public boolean onCommandRun(Commands command, ConsoleCommandResult consoleCommandResult) {
		return onCommandRun(command, consoleCommandResult, null);
	}

	@Override
	public boolean onCommandRun(Commands command, ConsoleCommandResult consoleCommandResult, CommandParameter parameter) {
		consoleCommandResult.setMessage(reactToCommand(command, profiler, stage));
		return true;
	}


	@Override
	public void onConsoleDeactivated() {

	}
}
