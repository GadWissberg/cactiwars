package com.gadarts.war.sound;

import com.badlogic.gdx.audio.Sound;

public class GameSound {
	private final Sound sound;
	private final SoundsDefinitions definition;

	public GameSound(Sound sound, SoundsDefinitions definition) {
		this.sound = sound;
		this.definition = definition;
	}

	public Sound getSound() {
		return sound;
	}

	public SoundsDefinitions getDefinition() {
		return definition;
	}
}
