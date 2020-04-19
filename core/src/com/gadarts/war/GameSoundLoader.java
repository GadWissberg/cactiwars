package com.gadarts.war;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.gadarts.shared.console.Console;
import com.gadarts.war.menu.console.ConsoleImpl;
import com.gadarts.war.sound.GameSound;
import com.gadarts.war.sound.SoundsDefinitions;

public class GameSoundLoader extends AsynchronousAssetLoader<GameSound, GameSoundLoader.GameSoundParameter> {
	public static final String MESSAGE_FAILED_LOAD_SOUND_NO_DEFINITION_FOUND = "Failed to load sound: %s, no definition was found.";
	private final ConsoleImpl console;

	public GameSoundLoader(FileHandleResolver resolver, ConsoleImpl consoleImpl) {
		super(resolver);
		this.console = consoleImpl;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, GameSoundParameter parameter) {
		return null;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, GameSoundParameter parameter) {

	}

	@Override
	public GameSound loadSync(AssetManager manager, String fileName, FileHandle file, GameSoundParameter parameter) {
		GameSound gameSound = null;
		try {
			SoundsDefinitions definition = SoundsDefinitions.valueOf(file.nameWithoutExtension().toUpperCase());
			Sound sound = Gdx.audio.newSound(file);
			gameSound = new GameSound(sound, definition);
		} catch (IllegalArgumentException e) {
			console.insertNewLog(MESSAGE_FAILED_LOAD_SOUND_NO_DEFINITION_FOUND, true, Console.ERROR_COLOR);
			e.printStackTrace();
		}
		return gameSound;
	}

	public static class GameSoundParameter extends AssetLoaderParameters<GameSound> {
	}
}
