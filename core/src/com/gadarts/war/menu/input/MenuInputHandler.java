package com.gadarts.war.menu.input;

import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;
import java.util.List;

public class MenuInputHandler implements InputProcessor {
    private List<MenuInputEventsSubscriber> subscribers = new ArrayList<>();

    @Override
    public boolean keyDown(int i) {
        for (MenuInputEventsSubscriber subscriber : subscribers) {
            subscriber.onKeyDown(i);
        }
        return true;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }

    public void subscribeForInputEvents(MenuInputEventsSubscriber subscriber) {
        if (subscribers.contains(subscriber)) return;
        subscribers.add(subscriber);
    }
}
