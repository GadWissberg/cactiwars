package com.gadarts.war.sound;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameSettings;

public class SoundPlayer {
    public long play(Sound sound) {
        return play(sound, false);
    }

    public long play(Sound sound, boolean loop) {
        if (!GameSettings.ALLOW_SOUND) return -1;
        long id;
        if (loop) {
            id = sound.loop(1, 1, 0);
        } else {
            id = sound.play(1, MathUtils.random(0.8f, 1.2f), 0);
        }
        return id;
    }

    public long play(SFX sound) {
        return play(GameAssetManager.getInstance().get(sound.getFileName(), Sound.class));
    }

    public long playRandomByDefinitions(SFX sound1, SFX sound2) {
        return play(MathUtils.randomBoolean() ? sound1 : sound2);
    }

    public void pauseSound(SFX sound) {
        pauseSound(GameAssetManager.getInstance().get(sound.getFileName(), Sound.class));
    }

    public void pauseSound(Sound sound) {
        pauseSound(sound, -1);
    }

    public void pauseSound(Sound sound, long id) {
        if (id < 0) {
            sound.pause();
        } else {
            sound.pause(id);
        }
    }

    public void resumeSound(Sound sound) {
        resumeSound(sound, -1);
    }

    public void resumeSound(Sound sound, long id) {
        if (id < 0) {
            sound.resume();
        } else {
            sound.resume(id);
        }
    }

    public void resumeSound(SFX sound) {
        resumeSound(GameAssetManager.getInstance().get(sound.getFileName(), Sound.class));
    }
}
