package com.gadarts.war.sound;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.GameAssetManager;

import java.util.Arrays;
import java.util.HashMap;

public class SoundPlayer {

	private static Vector3 auxVector = new Vector3();
	private static Plane auxPlane = new Plane();
	private HashMap<SoundTypes, Boolean> typesEnabled = new HashMap<>();
	private Array<SoundPlayerEventsSubscriber> subscribers = new Array<SoundPlayerEventsSubscriber>();

	public SoundPlayer() {
		Arrays.stream(SoundTypes.values()).forEach(sound -> typesEnabled.put(sound, DefaultGameSettings.ALLOW_SOUND));
	}

	public static float calculatePan(PerspectiveCamera camera, Vector3 soundSourcePosition) {
		auxPlane.set(camera.position, auxVector.set(camera.direction).crs(camera.up));
		float value = auxPlane.normal.dot(soundSourcePosition) + auxPlane.d;
		return (value < 0 ? -1 : 1) * MathUtils.norm(value >= 0 ? 0 : 0, value >= 0 ? 15f : -15f, value);
	}

	public void pauseSound(SoundsDefinitions sound) {
		pauseSound(GameAssetManager.getInstance().get(sound.getFileName(), GameSound.class));
	}

	public void pauseSound(GameSound sound) {
		pauseSound(sound, -1);
	}

	public void pauseSound(GameSound sound, long id) {
		Sound soundObject = sound.getSound();
		if (id < 0) {
			soundObject.pause();
		} else {
			soundObject.pause(id);
		}
	}

	public void resumeSound(GameSound sound) {
		resumeSound(sound, -1);
	}

	public void resumeSound(GameSound sound, long id) {
		if (id < 0) {
			sound.getSound().resume();
		} else {
			sound.getSound().resume(id);
		}
	}

	public void resumeSound(SoundsDefinitions sound) {
		resumeSound(GameAssetManager.getInstance().get(sound.getFileName(), GameSound.class));
	}

	public long play(SoundsDefinitions sound) {
		return play(sound, false);
	}

	public long play(SoundsDefinitions sound, boolean loop) {
		GameSound gameSound = GameAssetManager.getInstance().get(sound.getFileName(), GameSound.class);
		return play(gameSound, loop, 1);
	}

	public long play(GameSound sound, boolean loop) {
		return play(sound, loop, 1);
	}

	public long play(GameSound sound, boolean loop, float volume) {
		return playGameSound(sound, loop, volume, 1);
	}

	public long playWithPositionRandom(SoundsDefinitions sound1,
									   SoundsDefinitions sound2,
									   PerspectiveCamera camera,
									   Vector3 soundSourcePosition) {
		SoundsDefinitions soundsDefinition = MathUtils.randomBoolean() ? sound1 : sound2;
		return playWithPosition(soundsDefinition, camera, soundSourcePosition);
	}


	public long playWithPosition(SoundsDefinitions soundDef, PerspectiveCamera camera, Vector3 soundSourcePosition) {
		GameSound gameSound = GameAssetManager.getInstance().get(soundDef.getFileName(), GameSound.class);
		return playWithPosition(gameSound, false, camera, soundSourcePosition);
	}

	private long playWithPosition(GameSound gameSound,
								  boolean loop,
								  PerspectiveCamera camera,
								  Vector3 soundSourcePosition) {
		if (!typesEnabled.get(gameSound.getDefinition().getType())) return -1;
		boolean dynamicSound = camera != null && soundSourcePosition != null;
		float volume = dynamicSound ? calculateVolume(camera, soundSourcePosition) : 1;
		float pan = dynamicSound ? calculatePan(camera, soundSourcePosition) : 0f;
		long id = playGameSound(gameSound, loop, volume, pan);
		return id;
	}

	private long playGameSound(GameSound gameSound, boolean loop, float volume, float pan) {
		if (!typesEnabled.get(gameSound.getDefinition().getType())) return -1;
		Sound sound = gameSound.getSound();
		return loop ? sound.loop(volume, 1, pan) : sound.play(volume, MathUtils.random(0.8f, 1.2f), pan);
	}

	private float calculateVolume(PerspectiveCamera camera, Vector3 soundSourcePosition) {
		float dst = soundSourcePosition.dst2(camera.position);
		float v = 100f / (2 * dst);
		return MathUtils.norm(0, 1, v);
	}

	public void setEnabled(boolean enabled) {
		typesEnabled.forEach((type, value) -> setEnabled(type, enabled));
	}

	public void setEnabled(SoundTypes type, boolean enabled) {
		Boolean oldValue = typesEnabled.getOrDefault(type, false);
		typesEnabled.put(type, enabled);
		if (enabled) {
			if (!oldValue) subscribers.forEach(sub -> sub.soundTypeStateChanged(type, true));
		} else if (oldValue) {
			stopSoundByType(type);
			subscribers.forEach(sub -> sub.soundTypeStateChanged(type, false));
		}
	}

	private void stopSoundByType(SoundTypes type) {
		Array<GameSound> out = new Array<>();
		GameAssetManager.getInstance().getAll(GameSound.class, out);
		Arrays.stream(out.toArray())
				.filter(sound -> sound.getDefinition().getType() == type)
				.forEach(sound -> sound.getSound().stop());
	}

	public void subscribeForEvents(SoundPlayerEventsSubscriber subscriber) {
		if (subscribers.contains(subscriber, true)) return;
		subscribers.add(subscriber);
	}

	public boolean isTypeEnabled(SoundTypes type) {
		return typesEnabled.getOrDefault(type, false);
	}
}
