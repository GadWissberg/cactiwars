package com.gadarts.war.screens;

import com.badlogic.gdx.Screen;
import com.gadarts.war.sound.SoundPlayer;

public abstract class BaseGameScreen implements Screen {
    private SoundPlayer soundPlayer;

    @Override
    public void show() {

    }

    public SoundPlayer getSoundPlayer() {
        return soundPlayer;
    }

    public void setSoundPlayer(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void render(float v) {

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public abstract void onEscPressed();
}
