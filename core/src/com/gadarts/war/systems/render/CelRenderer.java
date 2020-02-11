package com.gadarts.war.systems.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.systems.render.cel.CelDepthShaderProvider;
import com.gadarts.war.systems.render.cel.CelLineShaderProgram;

import static com.gadarts.war.systems.render.RenderSystem.resetDisplay;

public class CelRenderer {
	private FrameBuffer celShaderFbo;
	private ModelBatch depthBatch = new ModelBatch(new CelDepthShaderProvider());
	private SpriteBatch spriteBatch = new SpriteBatch();
	private ShaderProgram lineShader = new CelLineShaderProgram();

	public void initialize(int width, int height) {
		if (DefaultGameSettings.CEL_SHADING) {
			celShaderFbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
		}
	}

	public void renderDepth(PerspectiveCamera camera, CelRendererUser celRendererUser, float deltaTime) {
		if (DefaultGameSettings.CEL_SHADING) {
			celShaderFbo.begin();
			resetDisplay(Color.CLEAR);
			depthBatch.begin(camera);
			celRendererUser.renderDepthWithInstances(depthBatch, deltaTime);
			depthBatch.end();
			celShaderFbo.end();
		}
	}

	public void renderOutline() {
		if (DefaultGameSettings.CEL_SHADING) {
			spriteBatch.setShader(lineShader);
			spriteBatch.begin();
			spriteBatch.draw(celShaderFbo.getColorBufferTexture(), 0, 0, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1, 1, 0, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);
			spriteBatch.end();
			spriteBatch.setShader(null);
		}
	}

	public void dispose() {
		depthBatch.dispose();
		spriteBatch.dispose();
		celShaderFbo.dispose();
		lineShader.dispose();
	}

	public void onResize(int width, int height) {
		celShaderFbo.dispose();
		initialize(width, height);
	}
}
