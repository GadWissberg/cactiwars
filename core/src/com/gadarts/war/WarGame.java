package com.gadarts.war;

import com.badlogic.gdx.Game;
import com.gadarts.shared.par.MainParLoadingFailureException;

import java.io.IOException;

public class WarGame extends Game {
    private BattleScreen screen;

    @Override
    public void create() {
        try {
            GameAssetManager.getInstance().loadAssets();
            screen = new BattleScreen();
            setScreen(screen);
        } catch (IOException | MainParLoadingFailureException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        GameAssetManager.getInstance().dispose();
    }
}
