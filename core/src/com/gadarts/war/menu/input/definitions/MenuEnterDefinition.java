package com.gadarts.war.menu.input.definitions;

import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.menu.GameMenuOption;
import com.gadarts.war.menu.MenuTable;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.sound.SoundPlayer;
import com.gadarts.war.sound.SoundsDefinitions;

public class MenuEnterDefinition implements MenuInputDefinition {
	@Override
	public void execute(GameMenu gameMenu, BaseGameScreen parentScreen) {
        if (!gameMenu.isVisible()) return;
        SoundPlayer soundPlayer = parentScreen.getSoundPlayer();
		soundPlayer.play(SoundsDefinitions.MENU_SELECT);
        MenuTable menuTable = gameMenu.getMenuTable();
        GameMenuOption option = (GameMenuOption) menuTable.getOptionsTable().getChildren().get(gameMenu.getSelected());
        option.getOptionDefinition().execute();
    }
}
