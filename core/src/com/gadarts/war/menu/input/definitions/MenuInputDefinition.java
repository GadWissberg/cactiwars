package com.gadarts.war.menu.input.definitions;

import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.screens.BaseGameScreen;

public interface MenuInputDefinition {
    void execute(GameMenu gameMenu, BaseGameScreen parentScreen);
}
