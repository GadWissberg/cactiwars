package com.gadarts.war.factories;

import com.gadarts.shared.definitions.character.CharacterAdditionalDefinition;
import com.gadarts.shared.definitions.character.CharacterDefinition;

import java.util.List;

public class PlayerProperties {
	private float x;
	private float y;
	private float z;
	private float rotation;
	private List<CharacterAdditionalDefinition> additionals;
	private CharacterDefinition characterDefinition;
	private String animationId;

	public CharacterDefinition getCharacterDefinition() {
		return characterDefinition;
	}

	public void setCharacterDefinition(CharacterDefinition characterDefinition) {
		this.characterDefinition = characterDefinition;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public List<CharacterAdditionalDefinition> getAdditionals() {
		return additionals;
	}

	public void setAdditionals(List<CharacterAdditionalDefinition> additionals) {
		this.additionals = additionals;
	}

	public String getAnimationId() {
		return animationId;
	}

	public void setAnimationId(String animationId) {
		this.animationId = animationId;
	}
}
