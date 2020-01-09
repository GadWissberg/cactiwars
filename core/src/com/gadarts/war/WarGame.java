package com.gadarts.war;

import com.badlogic.gdx.Game;
import com.gadarts.shared.par.MainParLoadingFailureException;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.sound.SoundPlayer;

import java.io.IOException;

public class WarGame extends Game {
    private BaseGameScreen screen;

    @Override
    public void create() {
        try {
            GameAssetManager.getInstance().loadAssets();
            SoundPlayer soundPlayer = new SoundPlayer();
            screen = GameSettings.INITIAL_SCREEN.getScreenClass().newInstance();
            screen.setSoundPlayer(soundPlayer);
            setScreen(screen);
        } catch (IOException | MainParLoadingFailureException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        GameAssetManager.getInstance().dispose();
    }
}
