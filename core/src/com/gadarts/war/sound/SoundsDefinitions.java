package com.gadarts.war.sound;

import com.gadarts.war.GameC;

public enum SoundsDefinitions {
	AMB_WIND(SoundTypes.AMBIANCE),
	MENU_MOVE(SoundTypes.MENU),
	MENU_SELECT(SoundTypes.MENU),
	HEAVY_METAL_CRASH_1(SoundTypes.MISC),
	HEAVY_METAL_CRASH_2(SoundTypes.MISC),
	LIGHT_METAL_CRASH_1(SoundTypes.MISC),
	LIGHT_METAL_CRASH_2(SoundTypes.MISC),
	CANNON_SHOOT_1(SoundTypes.WEAPONRY),
	CANNON_SHOOT_2(SoundTypes.WEAPONRY),
	ENGINE(SoundTypes.CHARACTER);

	private final String fileName;
	private final SoundTypes type;

	SoundsDefinitions(SoundTypes type) {
		this.fileName = GameC.Files.Sound.FOLDER_PATH + "/" + name().toLowerCase() + ".wav";
		this.type = type;
	}

	public SoundTypes getType() {
		return type;
	}

	public String getFileName() {
		return fileName;
	}
}
