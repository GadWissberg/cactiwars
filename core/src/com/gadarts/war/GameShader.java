package com.gadarts.war;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;

public class GameShader extends DefaultShader {
	public GameShader(Renderable renderable, Config config) {
		super(renderable, config);
	}

	@Override
	public void init() {
		super.init();
		if (program.getLog().length() != 0)
			System.out.println(program.getLog());
	}
}