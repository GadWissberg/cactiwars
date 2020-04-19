package com.gadarts.war.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.menu.hud.MenuEventsSubscriber;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.sound.SoundPlayerEventsSubscriber;
import com.gadarts.war.sound.SoundTypes;
import com.gadarts.war.sound.SoundsDefinitions;

public class EnvironmentSystem extends GameEntitySystem implements MenuEventsSubscriber, EntityListener, SoundPlayerEventsSubscriber {
	private final SoundPlayer soundPlayer;
	private long ambSoundId;

	public EnvironmentSystem(SoundPlayer soundPlayer) {
		this.soundPlayer = soundPlayer;
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(this);
		soundPlayer.subscribeForEvents(this);
		ambSoundId = soundPlayer.play(SoundsDefinitions.AMB_WIND, true);
	}

	@Override
	public void onMenuActivated() {
		soundPlayer.pauseSound(SoundsDefinitions.AMB_WIND);
	}

	@Override
	public void onMenuDeactivated() {
		soundPlayer.resumeSound(SoundsDefinitions.AMB_WIND);
	}

	@Override
	public void onResize(int width, int height) {

	}

	@Override
	public void entityAdded(Entity entity) {

	}

	@Override
	public void entityRemoved(Entity entity) {
		if (ComponentsMapper.environmentObject.has(entity)) {

		}
	}

	@Override
	public void soundTypeStateChanged(SoundTypes type, boolean enabled) {
		if (type == SoundTypes.AMBIANCE && enabled) {
			ambSoundId = soundPlayer.play(SoundsDefinitions.AMB_WIND, true);
		}
	}
}
