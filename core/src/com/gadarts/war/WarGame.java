package com.gadarts.war;

import com.badlogic.gdx.Game;

public class WarGame extends Game {
    private BattleScreen screen;

    @Override
    public void create() {
        screen = new BattleScreen();
        setScreen(screen);
    }

    @Override
    public void dispose() {
        GameAssetManager.getInstance().dispose();
    }
}
