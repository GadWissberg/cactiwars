package com.gadarts.war.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.menu.hud.MenuEventsSubscriber;
import com.gadarts.war.sound.SFX;
import com.gadarts.war.sound.SoundPlayer;

public class EnvironmentSystem extends GameEntitySystem implements MenuEventsSubscriber, EntityListener {
	private final SoundPlayer soundPlayer;

	public EnvironmentSystem(SoundPlayer soundPlayer) {
		this.soundPlayer = soundPlayer;
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(this);
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

	@Override
	public void entityAdded(Entity entity) {

	}

	@Override
	public void entityRemoved(Entity entity) {
		if (ComponentsMapper.environmentObject.has(entity)) {

		}
	}
}
