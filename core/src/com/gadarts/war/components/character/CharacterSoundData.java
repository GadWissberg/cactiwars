package com.gadarts.war.components.character;

import com.badlogic.gdx.audio.Sound;

public class CharacterSoundData {
    private long engineSoundId = -1;
    private Sound engineSound;
    private float enginePitch = 1;

    public Sound getEngineSound() {
        return engineSound;
    }

    public void setEngineSound(Sound engineSound) {
        this.engineSound = engineSound;
    }

    public long getEngineSoundId() {
        return engineSoundId;
    }

    public void setEngineSoundId(long engineSoundId) {
        this.engineSoundId = engineSoundId;
    }

    public float getEnginePitch() {
        return enginePitch;
    }

    public void setEngineWorkPitch(float enginePitch) {
        this.enginePitch = enginePitch;
    }

}
