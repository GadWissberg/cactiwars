package com.gadarts.war.menu.input.definitions;

import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.sound.SoundsDefinitions;

public class MenuArrowDownDefinition implements MenuInputDefinition {
	@Override
	public void execute(GameMenu gameMenu, BaseGameScreen parentScreen) {
		parentScreen.getSoundPlayer().play(SoundsDefinitions.MENU_MOVE);
		int selected = gameMenu.getSelected();
		if (selected == gameMenu.getMenuTable().getNumberOfOptions() - 1) {
			gameMenu.setSelected(0);
		} else {
			gameMenu.setSelected(selected + 1);
		}
		gameMenu.update();
	}
}
