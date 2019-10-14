package com.gadarts.war.systems.player.input;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.definitions.down.Esc;
import com.gadarts.war.systems.player.input.definitions.down.arrows.ArrowDownDown;
import com.gadarts.war.systems.player.input.definitions.down.arrows.ArrowLeftDown;
import com.gadarts.war.systems.player.input.definitions.down.arrows.ArrowRightDown;
import com.gadarts.war.systems.player.input.definitions.down.arrows.ArrowUpDown;
import com.gadarts.war.systems.player.input.definitions.up.ArrowDownUp;
import com.gadarts.war.systems.player.input.definitions.up.ArrowLeftUp;
import com.gadarts.war.systems.player.input.definitions.up.ArrowRightUp;
import com.gadarts.war.systems.player.input.definitions.up.ArrowUpUp;

import java.util.List;

public enum KeyMap {
    ARROW_UP(Input.Keys.UP, new ArrowUpDown(), new ArrowUpUp()),
    ARROW_DOWN(Input.Keys.DOWN, new ArrowDownDown(), new ArrowDownUp()),
    ARROW_LEFT(Input.Keys.LEFT, new ArrowLeftDown(), new ArrowLeftUp()),
    ARROW_RIGHT(Input.Keys.RIGHT, new ArrowRightDown(), new ArrowRightUp()),
    TEST(Input.Keys.SPACE, new InputEvent() {
        @Override
        public boolean run(Entity entity, List<PlayerSystemEventsSubscriber> subscribers) {
            ComponentsMapper.physics.get(entity).getBody().applyCentralImpulse(new Vector3(0,1,-1).scl(20));
            return true;
        }
    }),
    ESC(Input.Keys.ESCAPE, new Esc());

    private final int keyCode;
    private final InputEvent keyDown;
    private final InputEvent keyUp;

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

    KeyMap(int keyCode, InputEvent keyDown) {
        this(keyCode, keyDown, null);
    }

    KeyMap(int keyCode, InputEvent keyDown, InputEvent keyUp) {
        this.keyCode = keyCode;
        this.keyDown = keyDown;
        this.keyUp = keyUp;
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
