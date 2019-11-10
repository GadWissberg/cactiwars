package com.gadarts.war.systems;

import com.badlogic.ashley.core.PooledEngine;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystem;

public class SystemsHandler {
    private final PooledEngine engine;
    private final SoundPlayer soundPlayer;

    public SystemsHandler(PooledEngine engine, SoundPlayer soundPlayer) {
        this.engine = engine;
        this.soundPlayer = soundPlayer;
    }

    public void init() {
        CameraSystem cameraSystem = createCameraSystem();
        RenderSystem renderSystem = createRenderSystem();
        PhysicsSystem physicsSystem = new PhysicsSystem(soundPlayer);
        physicsSystem.subscribeForEvents(renderSystem);
        PlayerSystem playerSystem = new PlayerSystem(soundPlayer);
        playerSystem.subscribeForEvents(cameraSystem);
        engine.addSystem(physicsSystem);
        engine.addSystem(playerSystem);
        engine.addSystem(new CharacterSystem(soundPlayer));
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
