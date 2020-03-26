package com.gadarts.war.sound;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.GameAssetManager;

public class SoundPlayer {

    private static Vector3 auxVector = new Vector3();
    private static Plane auxPlane = new Plane();

    public long play(Sound sound) {
        return play(sound, false);
    }

    public long play(Sound sound, boolean loop) {
        return play(sound, loop, null, null);
    }

    public long play(SFX sound) {
        return play(GameAssetManager.getInstance().get(sound.getFileName(), Sound.class));
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

    public long playRandomByDefinitions(SFX sound1, SFX sound2, PerspectiveCamera camera, Vector3 soundSourcePosition) {
        return play(MathUtils.randomBoolean() ? sound1 : sound2, camera, soundSourcePosition);
    }

    public static float calculatePan(PerspectiveCamera camera, Vector3 soundSourcePosition) {
        auxPlane.set(camera.position, auxVector.set(camera.direction).crs(camera.up));
        float value = auxPlane.normal.dot(soundSourcePosition) + auxPlane.d;
        return (value < 0 ? -1 : 1) * MathUtils.norm(value >= 0 ? 0 : 0, value >= 0 ? 15f : -15f, value);
    }

    public long play(SFX sound, PerspectiveCamera camera, Vector3 soundSourcePosition) {
        return play(GameAssetManager.getInstance().get(sound.getFileName(), Sound.class), camera, soundSourcePosition);
    }

    public long play(Sound sound, PerspectiveCamera camera, Vector3 soundSourcePosition) {
        return play(sound, false, camera, soundSourcePosition);
    }

    public long play(Sound sound, boolean loop, PerspectiveCamera camera, Vector3 soundSourcePosition) {
		if (!DefaultGameSettings.ALLOW_SOUND) return -1;
        long id;
        boolean dynamicSound = camera != null && soundSourcePosition != null;
        float volume = dynamicSound ? calculateVolume(camera, soundSourcePosition) : 1f;
        float pan = dynamicSound ? calculatePan(camera, soundSourcePosition) : 0f;
        if (loop) id = sound.loop(volume, 1, pan);
        else id = sound.play(volume, MathUtils.random(0.8f, 1.2f), pan);
        return id;
    }

    private float calculateVolume(PerspectiveCamera camera, Vector3 soundSourcePosition) {
        float dst = soundSourcePosition.dst2(camera.position);
        float v = 1000f / (dst * dst);
        return MathUtils.norm(0, 2, v);
    }
}
