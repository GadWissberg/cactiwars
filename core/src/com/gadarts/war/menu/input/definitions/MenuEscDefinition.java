package com.gadarts.war.menu.input.definitions;

import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.screens.BaseGameScreen;

public class MenuEscDefinition implements MenuInputDefinition {
    @Override
    public void execute(GameMenu gameMenu, BaseGameScreen parentScreen) {
        parentScreen.onEscPressed();
    }
}
