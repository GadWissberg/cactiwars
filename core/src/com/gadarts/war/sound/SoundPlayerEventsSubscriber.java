package com.gadarts.war.sound;

public interface SoundPlayerEventsSubscriber {
	void soundTypeStateChanged(SoundTypes type, boolean enabled);
}
