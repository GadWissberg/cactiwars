package com.gadarts.war.systems.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.war.BattleScreen;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.systems.player.input.InputEvent;
import com.gadarts.war.systems.player.input.KeyMap;
import com.gadarts.war.systems.player.input.PlayerInputProcessor;

import java.util.ArrayList;
import java.util.List;

public class PlayerSystem extends EntitySystem implements PlayerInputProcessor, PlayerSystemEventsSubscriber {
    public static Vector3 auxVector = new Vector3();
    public static Vector3 auxVector2 = new Vector3();
    private final SoundPlayer soundPlayer;
    private Entity player;
    private Matrix4 auxMat = new Matrix4();
    private List<PlayerSystemEventsSubscriber> subscribers = new ArrayList<>();

    public PlayerSystem(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        subscribeForEvents(this);
    }


    @Override
    public void update(float deltaTime) {
        if (!BattleScreen.isPaused()) {
            super.update(deltaTime);
        }
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    @Override
    public boolean onKeyDown(KeyMap keyMap) {
        keyMap.getKeyDown().execute(player, subscribers);
        return true;
    }

    @Override
    public boolean onKeyUp(KeyMap keyMap) {
        InputEvent keyUp = keyMap.getKeyUp();
        if (keyUp != null) {
            keyUp.execute(player, subscribers);
        }
        return true;
    }

    public void subscribeForEvents(PlayerSystemEventsSubscriber sub) {
        if (subscribers.contains(sub)) return;
        subscribers.add(sub);
    }

    @Override
    public void onMovementAccelerationBegan() {
    }

    @Override
    public void onMovementAccelerationStopped() {
    }
}
