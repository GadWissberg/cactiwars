package com.gadarts.war.menu.input.definitions;

import com.gadarts.war.GameScreen;
import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.menu.GameMenuOption;

public class MenuEnterDefinition implements MenuInputDefinition {
    @Override
    public void execute(GameMenu gameMenu, GameScreen parentScreen) {
        GameMenuOption option = (GameMenuOption) gameMenu.getOptionsTable().getChildren().get(gameMenu.getSelected());
        option.getOptionDefinition().execute();
    }
}
