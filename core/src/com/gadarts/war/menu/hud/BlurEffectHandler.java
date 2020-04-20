package com.gadarts.war.menu.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.gadarts.war.GameC;

import java.io.File;

public class BlurEffectHandler implements Disposable {
	private final static Vector2 blurMag = new Vector2(1, 1);

	private final ShaderProgram blurShaderProgram;
	private final FrameBuffer blurFrameBuffer;
	private final int blurRadiusLocation;
	private final int blurDirectionLocation;
	private final int blurResolutionLocation;

	public BlurEffectHandler() {
		String blurFragmentShader = Gdx.files.internal(GameC.Files.ASSETS_PATH + "shaders" + File.separator + "fragment_blur.glsl").readString();
		String blurVertexShader = Gdx.files.internal(GameC.Files.ASSETS_PATH + "shaders" + File.separator + "vertex_blur.glsl").readString();
		blurShaderProgram = new ShaderProgram(blurVertexShader, blurFragmentShader);
		blurFrameBuffer = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		blurRadiusLocation = blurShaderProgram.getUniformLocation(GameC.Menu.BlurShaderKeys.BLUR_RADIUS);
		blurDirectionLocation = blurShaderProgram.getUniformLocation(GameC.Menu.BlurShaderKeys.BLUR_DIRECTION);
		blurResolutionLocation = blurShaderProgram.getUniformLocation(GameC.Menu.BlurShaderKeys.BLUR_RESOLUTION);
	}

	public ShaderProgram getShaderProgram() {
		return blurShaderProgram;
	}

	public FrameBuffer getFrameBuffer() {
		return blurFrameBuffer;
	}

	public void refreshBlurUniforms() {
		blurMag.set(35f, 0f);
		blurShaderProgram.setUniformf(blurRadiusLocation, blurMag.len2());
		blurMag.nor();
		blurShaderProgram.setUniformf(blurDirectionLocation, blurMag.x, blurMag.y);
		blurShaderProgram.setUniformf(blurResolutionLocation, ((float) Gdx.graphics.getWidth()));
	}

	@Override
	public void dispose() {
		blurShaderProgram.dispose();
		blurFrameBuffer.dispose();
	}
}
