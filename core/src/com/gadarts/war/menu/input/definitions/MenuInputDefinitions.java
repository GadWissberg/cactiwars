package com.gadarts.war.menu.input.definitions;

import com.badlogic.gdx.Input;

public enum MenuInputDefinitions {
    DOWN(Input.Keys.DOWN, new MenuArrowDownDefinition()),
    UP(Input.Keys.UP, new MenuArrowUpDefinition()),
    ESC(Input.Keys.ESCAPE, new MenuEscDefinition()),
    ENTER(Input.Keys.ENTER, new MenuEnterDefinition());

    private final MenuInputDefinition def;
    private final int key;

    MenuInputDefinitions(int key, MenuInputDefinition menuInputDefinition) {
        this.key = key;
        this.def = menuInputDefinition;
    }

    public static MenuInputDefinitions findByKey(int key) {
        MenuInputDefinitions[] values = values();
        MenuInputDefinitions result = null;
        for (MenuInputDefinitions menuInputDefinition : values)
            if (menuInputDefinition.getKey() == key) {
                result = menuInputDefinition;
                break;
            }
        return result;
    }

    public int getKey() {
        return key;
    }

    public MenuInputDefinition getDef() {
        return def;
    }
}
