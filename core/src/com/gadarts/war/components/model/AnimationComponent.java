package com.gadarts.war.components.model;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Pool;

public class AnimationComponent implements Component, Pool.Poolable {
	private AnimationController animationController;

	@Override
	public void reset() {

	}

	public AnimationComponent init(ModelInstance modelInstance, String animationId) {
		animationController = new AnimationController(modelInstance);
		animationController.setAnimation(animationId, -1, 0.5f, new AnimationController.AnimationListener() {
			@Override
			public void onEnd(AnimationController.AnimationDesc animationDesc) {

			}

			@Override
			public void onLoop(AnimationController.AnimationDesc animationDesc) {

			}
		});
		return this;
	}

	public AnimationController getController() {
		return animationController;
	}
}
