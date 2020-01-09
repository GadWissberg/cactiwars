package com.gadarts.war;

import com.badlogic.ashley.core.PooledEngine;
import com.gadarts.war.sound.SoundPlayer;

public interface InGameScreen {
    void resumeGame();

    void pauseGame();


    PooledEngine getEntitiesEngine();

    SoundPlayer getSoundPlayer();
}
