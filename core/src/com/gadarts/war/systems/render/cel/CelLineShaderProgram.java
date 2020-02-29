package com.gadarts.war.systems.render.cel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.gadarts.war.GameC.Files;

public class CelLineShaderProgram extends ShaderProgram {
	public CelLineShaderProgram() {
		super(Gdx.files.internal(Files.ASSETS_PATH + "shaders/vertex_cel.glsl"), Gdx.files.internal(Files.ASSETS_PATH + "shaders/fragment_cel.glsl"));
	}

	@Override
	public void begin() {
		super.begin();
		setUniformf("u_size", (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
	}
}
