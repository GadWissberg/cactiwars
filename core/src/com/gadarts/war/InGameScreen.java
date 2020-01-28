package com.gadarts.war;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gadarts.war.sound.SoundPlayer;

public interface InGameScreen {
	void resumeGame();

	void pauseGame();

	Stage getHudStage();

	PooledEngine getEntitiesEngine();

	SoundPlayer getSoundPlayer();
}
