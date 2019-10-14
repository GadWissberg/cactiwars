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
        CameraSystem cameraSystem = new CameraSystem();
        engine.addSystem(cameraSystem);
        engine.addSystem(new RenderSystem());
        engine.addSystem(new PhysicsSystem(soundPlayer));
        PlayerSystem playerSystem = new PlayerSystem(soundPlayer);
        playerSystem.subscribeForEvents(cameraSystem);
        engine.addSystem(playerSystem);
        engine.addSystem(new CharacterSystem(soundPlayer));
    }
}
