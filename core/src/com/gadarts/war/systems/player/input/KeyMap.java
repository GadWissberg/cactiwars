package com.gadarts.war.systems.player.input;

import com.badlogic.gdx.Input;
import com.gadarts.war.systems.player.input.definitions.down.Ctrl;
import com.gadarts.war.systems.player.input.definitions.down.Esc;
import com.gadarts.war.systems.player.input.definitions.down.Grave;
import com.gadarts.war.systems.player.input.definitions.down.Space;
import com.gadarts.war.systems.player.input.definitions.down.arrows.ArrowDownDown;
import com.gadarts.war.systems.player.input.definitions.down.arrows.ArrowLeftDown;
import com.gadarts.war.systems.player.input.definitions.down.arrows.ArrowRightDown;
import com.gadarts.war.systems.player.input.definitions.down.arrows.ArrowUpDown;
import com.gadarts.war.systems.player.input.definitions.up.ArrowDownUp;
import com.gadarts.war.systems.player.input.definitions.up.ArrowLeftUp;
import com.gadarts.war.systems.player.input.definitions.up.ArrowRightUp;
import com.gadarts.war.systems.player.input.definitions.up.ArrowUpUp;

public enum KeyMap {
	ARROW_UP(Input.Keys.UP, new ArrowUpDown(), new ArrowUpUp()),
	ARROW_DOWN(Input.Keys.DOWN, new ArrowDownDown(), new ArrowDownUp()),
	ARROW_LEFT(Input.Keys.LEFT, new ArrowLeftDown(), new ArrowLeftUp()),
	ARROW_RIGHT(Input.Keys.RIGHT, new ArrowRightDown(), new ArrowRightUp()),
	SPACE(Input.Keys.SPACE, new Space()),
	CTRL(Input.Keys.CONTROL_LEFT, new Ctrl()),
	ESC(Input.Keys.ESCAPE, new Esc()),
	GRAVE(Input.Keys.GRAVE, '`', new Grave());

	private final int keyCode;
	private final InputEvent keyDown;
	private final InputEvent keyUp;
	private final char asciiValue;

	KeyMap(int keyCode, InputEvent keyDown) {
		this(keyCode, (char) -1, keyDown, null);
	}

	KeyMap(int keyCode, char asciiValue, InputEvent keyDown) {
		this(keyCode, asciiValue, keyDown, null);
	}

	KeyMap(int keyCode, InputEvent keyDown, InputEvent keyUp) {
		this(keyCode, (char) -1, keyDown, keyUp);
	}

	KeyMap(int keyCode, char asciiValue, InputEvent keyDown, InputEvent keyUp) {
		this.keyCode = keyCode;
		this.keyDown = keyDown;
		this.keyUp = keyUp;
		this.asciiValue = asciiValue;
	}

	public static KeyMap findKeyMapByKeyCode(int keycode) {
		KeyMap result = null;
		for (KeyMap keyMap : KeyMap.values()) {
			if (keyMap.getKeyCode() == keycode) {
				result = keyMap;
				break;
			}
		}
		return result;
	}

	public char getAsciiValue() {
		return asciiValue;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public InputEvent getKeyDown() {
		return keyDown;
	}

	public InputEvent getKeyUp() {
		return keyUp;
	}
}
