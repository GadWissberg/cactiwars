package com.gadarts.war;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

public class GameShaderProvider extends DefaultShaderProvider {
    private GameShader.Config config = new GameShader.Config();

    public GameShaderProvider() {
        config.vertexShader = Gdx.files.internal("vertex_shader.glsl").readString();
        config.fragmentShader = Gdx.files.internal("fragment_shader.glsl").readString();
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        config.numPointLights = 50;
        return new GameShader(renderable, config);
    }
}
