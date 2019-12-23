package com.gadarts.war.menu.input.definitions;

import com.gadarts.war.GameScreen;
import com.gadarts.war.menu.GameMenu;

public class MenuArrowDownDefinition implements MenuInputDefinition {
    @Override
    public void execute(GameMenu gameMenu, GameScreen parentScreen) {
        int selected = gameMenu.getSelected();
        if (selected == gameMenu.getOptionsTable().getChildren().size - 1) {
            gameMenu.setSelected(0);
        } else {
            gameMenu.setSelected(selected + 1);
        }
        gameMenu.update();
    }
}
