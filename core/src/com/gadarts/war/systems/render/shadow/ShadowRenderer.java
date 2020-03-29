package com.gadarts.war.systems.render.shadow;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.shared.SharedC;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.GameC;

@SuppressWarnings("deprecation")
public class ShadowRenderer {
	private static Vector3 auxVector31 = new Vector3();
	private final Environment environment;

	private ModelBatch shadowBatch;
	private MgsxDirectionalShadowLight shadowLight;
	private boolean enabled = !DefaultGameSettings.SKIP_DRAW_SHADOWS;

	public ShadowRenderer(Environment environment) {
		this.environment = environment;
		shadowLight = new MgsxDirectionalShadowLight(GameC.ShadowMap.SIZE, GameC.ShadowMap.SIZE,
				GameC.ShadowMap.VIEWPORT_SIZE, GameC.ShadowMap.VIEWPORT_SIZE, 1, SharedC.Camera.FAR);
		shadowLight.set(GameC.ShadowMap.COLOR, auxVector31.set(-0.5f, -1, -0.5f), 1);
		enableShadows();
		shadowBatch = new ModelBatch(new DepthShaderProvider());
	}

	private void enableShadows() {
		environment.add(shadowLight);
		environment.shadowMap = shadowLight;
	}

	public ModelBatch getShadowBatch() {
		return shadowBatch;
	}

	public MgsxDirectionalShadowLight getShadowLight() {
		return shadowLight;
	}

	public void begin(PerspectiveCamera camera) {
		shadowLight.begin();
		shadowLight.center.set(camera.position);
		Camera shadowLightCamera = shadowLight.getCamera();
		shadowBatch.begin(shadowLightCamera);
	}

	public void end() {
		shadowBatch.end();
		shadowLight.end();
	}

	public void dispose() {
		shadowBatch.dispose();
		shadowLight.dispose();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled) {
			disableShadows();
		} else {
			enableShadows();
		}
	}

	private void disableShadows() {
		environment.shadowMap = null;
		environment.remove(shadowLight);
	}
}
