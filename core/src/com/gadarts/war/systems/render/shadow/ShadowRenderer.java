package com.gadarts.war.systems.render.shadow;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.shared.SharedC;
import com.gadarts.war.GameC;

public class ShadowRenderer {
    private static Vector3 auxVector31 = new Vector3();

    private ModelBatch shadowBatch;
    private MgsxDirectionalShadowLight shadowLight;

    public ShadowRenderer(Environment environment) {
        shadowLight = new MgsxDirectionalShadowLight(GameC.ShadowMap.SIZE, GameC.ShadowMap.SIZE,
                GameC.ShadowMap.VIEWPORT_SIZE, GameC.ShadowMap.VIEWPORT_SIZE, 1, SharedC.Camera.FAR);
        shadowLight.set(GameC.ShadowMap.COLOR, auxVector31.set(-0.5f, -1, -0.5f), 1);
        environment.add(shadowLight);
        environment.shadowMap = shadowLight;
        shadowBatch = new ModelBatch(new DepthShaderProvider());
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

    public FrameBuffer getFrameBuffer() {
        return shadowLight.getFrameBuffer();
    }
}
