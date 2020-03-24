package com.gadarts.war.components.character;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Pool;
import com.gadarts.shared.definitions.character.CharacterAdditionalDefinition;

public class CharacterAdditional implements Pool.Poolable {
	private CharacterAdditionalDefinition definition;
	private AnimationController animationController;

	public CharacterAdditionalDefinition getDefinition() {
		return definition;
	}

	@Override
	public void reset() {

	}

	public void init(CharacterAdditionalDefinition additionalDefinition, ModelInstance additionalModelInstance) {
		this.definition = additionalDefinition;
		String animationId = additionalDefinition.getOnShootAnimationId();
		if (animationId != null) {
			//TODO: DON'T FORGET TO CHANGE THIS TO BE POOLED!!
			animationController = new AnimationController(additionalModelInstance);
			animationController.setAnimation(animationId, 0);
			animationController.allowSameAnimation = true;
		}
	}

	public AnimationController getAnimationController() {
		return animationController;
	}

	public void update(float deltaTime) {
		if (getDefinition().getOnShootAnimationId() != null) {
			getAnimationController().update(deltaTime);
		}
	}

	public void onShoot() {
		String onShootAnimationId = definition.getOnShootAnimationId();
		if (onShootAnimationId != null) {
			animationController.setAnimation(onShootAnimationId);
		}
	}
}
