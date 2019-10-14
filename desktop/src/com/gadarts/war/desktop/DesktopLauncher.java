package com.gadarts.war.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gadarts.shared.*;
import com.gadarts.war.GameSettings;
import com.gadarts.war.WarGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "WarGame";
        handleScreen(config);
        new LwjglApplication(new WarGame(), config);
        Gdx.app.setLogLevel(GameSettings.LOG_LEVEL);
    }

    private static void handleScreen(LwjglApplicationConfiguration config) {
        if (GameSettings.FULL_SCREEN) {
            setScreenSize(config, SharedC.Resolution.UI_WORLD_WIDTH, SharedC.Resolution.UI_WORLD_HEIGHT);
            config.fullscreen = true;
        } else {
            setScreenSize(config, 1280, 720);
        }
        config.samples = 3;
    }

    private static void setScreenSize(LwjglApplicationConfiguration config, int i, int i2) {
        config.width = i;
        config.height = i2;
    }
}
