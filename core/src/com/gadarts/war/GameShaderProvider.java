package com.gadarts.war;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.gadarts.war.GameC.Files;

import java.io.File;

public class GameShaderProvider extends DefaultShaderProvider {
    private GameShader.Config config = new GameShader.Config();

    public GameShaderProvider() {
		String vertexPath = Files.ASSETS_PATH + "shaders" + File.separator + "vertex_shader.glsl";
		config.vertexShader = Gdx.files.internal(vertexPath).readString();
		String fragmentPath = Files.ASSETS_PATH + "shaders" + File.separator + "fragment_shader.glsl";
		config.fragmentShader = Gdx.files.internal(fragmentPath).readString();

	}

    @Override
    protected Shader createShader(Renderable renderable) {
        config.numPointLights = 50;
        return new GameShader(renderable, config);
    }
}
