package com.gadarts.war.systems;

import com.gadarts.war.menu.hud.HudEventsSubscriber;
import com.gadarts.war.sound.SFX;
import com.gadarts.war.sound.SoundPlayer;

public class EnvironmentSystem extends GameEntitySystem implements HudEventsSubscriber {
	private final SoundPlayer soundPlayer;

	public EnvironmentSystem(SoundPlayer soundPlayer) {
		this.soundPlayer = soundPlayer;
	}

	@Override
	public void onMenuActivated() {
		soundPlayer.pauseSound(SFX.AMB_WIND);
	}

	@Override
	public void onMenuDeactivated() {
		soundPlayer.resumeSound(SFX.AMB_WIND);
	}

	@Override
	public void onResize(int width, int height) {

	}
}
