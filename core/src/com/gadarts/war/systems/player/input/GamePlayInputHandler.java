package com.gadarts.war.systems.player.input;

import com.badlogic.gdx.InputProcessor;
import com.gadarts.war.screens.BattleScreen;

import java.util.ArrayList;
import java.util.List;

public class GamePlayInputHandler implements InputProcessor  {

    private List<PlayerInputProcessor> subscribers = new ArrayList<>();

    @Override
    public boolean keyDown(int keycode) {
        if (BattleScreen.isPaused()) return false;
        for (PlayerInputProcessor sub : subscribers) {
            KeyMap keyMapByKeyCode = KeyMap.findKeyMapByKeyCode(keycode);
            if (keyMapByKeyCode != null) {
                sub.onKeyDown(keyMapByKeyCode);
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (BattleScreen.isPaused()) return false;
        for (PlayerInputProcessor sub : subscribers) {
            KeyMap keyMapByKeyCode = KeyMap.findKeyMapByKeyCode(keycode);
            if (keyMapByKeyCode != null) {
                sub.onKeyUp(KeyMap.findKeyMapByKeyCode(keycode));
            }
        }
		return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void subscribeForInputEvents(PlayerInputProcessor sub) {
        if (subscribers.contains(sub)) return;
        subscribers.add(sub);
    }
}
