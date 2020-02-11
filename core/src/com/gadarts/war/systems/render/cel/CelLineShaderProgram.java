package com.gadarts.war.systems.render.cel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class CelLineShaderProgram extends ShaderProgram {
	public CelLineShaderProgram() {
		super(Gdx.files.internal("shaders/vertex_cel.glsl"), Gdx.files.internal("shaders/fragment_cel.glsl"));
	}

	@Override
	public void begin() {
		super.begin();
		setUniformf("u_size", (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
	}
}
