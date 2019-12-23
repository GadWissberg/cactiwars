package com.gadarts.war.systems.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.gadarts.war.BattleScreen;
import com.gadarts.war.GameScreen;
import com.gadarts.war.systems.player.input.InputEvent;
import com.gadarts.war.systems.player.input.KeyMap;
import com.gadarts.war.systems.player.input.PlayerInputProcessor;

import java.util.ArrayList;
import java.util.List;

public class PlayerSystem extends EntitySystem implements PlayerInputProcessor, PlayerSystemEventsSubscriber {
    private final GameScreen parentScreen;
    private Entity player;
    private List<PlayerSystemEventsSubscriber> subscribers = new ArrayList<>();

    public PlayerSystem(GameScreen parentScreen) {
        this.parentScreen = parentScreen;
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
        keyMap.getKeyDown().execute(player, subscribers, parentScreen);
        return true;
    }

    @Override
    public boolean onKeyUp(KeyMap keyMap) {
        InputEvent keyUp = keyMap.getKeyUp();
        if (keyUp != null) {
            keyUp.execute(player, subscribers, parentScreen);
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
