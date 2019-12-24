package com.gadarts.war;

import com.badlogic.gdx.Screen;
import com.gadarts.war.sound.SoundPlayer;

public interface GameScreen extends Screen {
    void resumeGame();

    void pauseGame();

    SoundPlayer getSoundPlayer();
}
