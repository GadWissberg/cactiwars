package com.gadarts.war.sound;

import com.gadarts.war.GameC;

public enum SFX {
    HEAVY_METAL_CRASH_1,
    HEAVY_METAL_CRASH_2,
    LIGHT_METAL_CRASH_1,
    LIGHT_METAL_CRASH_2,
    ENGINE;

    private final String fileName;

    SFX() {
        this.fileName = GameC.Files.SOUNDS_FOLDER_NAME + "/" + name().toLowerCase() + ".wav";
    }

    public String getFileName() {
        return fileName;
    }
}
