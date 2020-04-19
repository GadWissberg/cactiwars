package com.gadarts.war.components.character;

import com.gadarts.war.sound.GameSound;

public class CharacterSoundData {
    private long engineSoundId = -1;
    private GameSound engineSound;
    private float enginePitch = 1;

    public GameSound getEngineSound() {
        return engineSound;
    }

    public void setEngineSound(GameSound engineSound) {
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
