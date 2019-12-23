package com.gadarts.war.menu.input.definitions;

import com.gadarts.war.GameScreen;
import com.gadarts.war.menu.GameMenu;

public class MenuEscDefinition implements MenuInputDefinition {
    @Override
    public void execute(GameMenu gameMenu, GameScreen parentScreen) {
        parentScreen.resumeGame();
    }
}
