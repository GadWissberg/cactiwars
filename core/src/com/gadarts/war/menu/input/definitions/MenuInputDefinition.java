package com.gadarts.war.menu.input.definitions;

import com.gadarts.war.GameScreen;
import com.gadarts.war.menu.GameMenu;

public interface MenuInputDefinition {
    void execute(GameMenu gameMenu, GameScreen parentScreen);
}
