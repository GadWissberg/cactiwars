package com.gadarts.war.systems.render;

import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface CelRendererUser {
	void renderDepthWithInstances(ModelBatch depthBatchToRenderWith, float deltaTime);
}
