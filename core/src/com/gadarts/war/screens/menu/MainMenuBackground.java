package com.gadarts.war.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class MainMenuBackground {

    private static Vector2 auxVector = new Vector2();
    private final ShaderProgram shaderProgram;
    private final Texture texture;
    private float time;

    public MainMenuBackground(Texture background, ShaderProgram backgroundShaderProgram) {
        this.texture = background;
        this.shaderProgram = backgroundShaderProgram;
        auxVector.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void draw(Batch batch) {
        batch.begin();
        batch.setShader(shaderProgram);
        time += 0.1f;
        shaderProgram.setUniformf("resolution", auxVector);
        shaderProgram.setUniformf("time", time);
        batch.draw(texture, 0, 0);
        batch.setShader(null);
        batch.end();
    }
}
