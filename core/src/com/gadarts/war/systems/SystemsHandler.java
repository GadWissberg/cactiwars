package com.gadarts.war.systems;

import com.badlogic.ashley.core.PooledEngine;
import com.gadarts.war.InGameScreen;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystem;
import com.gadarts.war.systems.render.RenderSystem;

public class SystemsHandler {
	private final PooledEngine engine;
	private final SoundPlayer soundPlayer;

	public SystemsHandler(PooledEngine engine, SoundPlayer soundPlayer) {
		this.engine = engine;
		this.soundPlayer = soundPlayer;
	}

	public void init(BaseGameScreen parentScreen) {
		CameraSystem cameraSystem = createCameraSystem();
		RenderSystem renderSystem = createRenderSystem();
		PhysicsSystem physicsSystem = new PhysicsSystem(soundPlayer);
		physicsSystem.subscribeForEvents(renderSystem);
		PlayerSystem playerSystem = new PlayerSystem((InGameScreen) parentScreen);
		playerSystem.subscribeForEvents(cameraSystem);
		engine.addSystem(physicsSystem);
		engine.addSystem(playerSystem);
		engine.addSystem(new CharacterSystem(parentScreen.getSoundPlayer(), ((InGameScreen) parentScreen).getActorFactory()));
		engine.addSystem(new EnvironmentSystem(parentScreen.getSoundPlayer()));
	}

	private RenderSystem createRenderSystem() {
		RenderSystem renderSystem = new RenderSystem();
		engine.addSystem(renderSystem);
		return renderSystem;
	}

	private CameraSystem createCameraSystem() {
		CameraSystem cameraSystem = new CameraSystem();
		engine.addSystem(cameraSystem);
		return cameraSystem;
	}
}
