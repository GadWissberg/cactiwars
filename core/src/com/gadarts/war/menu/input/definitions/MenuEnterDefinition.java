package com.gadarts.war.menu.input.definitions;

import com.badlogic.gdx.audio.Sound;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.menu.GameMenuOption;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.sound.SFX;

public class MenuEnterDefinition implements MenuInputDefinition {
    @Override
    public void execute(GameMenu gameMenu, BaseGameScreen parentScreen) {
        parentScreen.getSoundPlayer().play(GameAssetManager.getInstance().get(SFX.MENU_SELECT.getFileName(), Sound.class));
        GameMenuOption option = (GameMenuOption) gameMenu.getOptionsTable().getChildren().get(gameMenu.getSelected());
        option.getOptionDefinition().execute();
    }
}
