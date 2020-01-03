package com.gadarts.war.systems.render.cel;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;

public class CelDepthShaderProvider extends BaseShaderProvider {

    @Override
    protected Shader createShader(Renderable renderable) {
        return new CelDepthShader(renderable);
    }
}
