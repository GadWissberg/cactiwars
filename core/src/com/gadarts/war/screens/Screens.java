package com.gadarts.war.screens;

import com.gadarts.war.screens.menu.MainMenuScreen;

public enum Screens {
    MAIN_MENU(MainMenuScreen.class), BATTLE(BattleScreen.class);

    private final Class<? extends BaseGameScreen> screenClass;

    Screens(Class<? extends BaseGameScreen> screenClass) {
        this.screenClass = screenClass;
    }

    public Class<? extends BaseGameScreen> getScreenClass() {
        return screenClass;
    }
}
