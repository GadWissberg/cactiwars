package com.gadarts.war.menu.input.definitions;

import com.gadarts.war.GameScreen;
import com.gadarts.war.menu.GameMenu;

public class MenuArrowUpDefinition implements MenuInputDefinition {
    @Override
    public void execute(GameMenu gameMenu, GameScreen parentScreen) {
        int selected = gameMenu.getSelected();
        if (selected == 0) {
            gameMenu.setSelected(gameMenu.getOptionsTable().getChildren().size - 1);
        } else {
            gameMenu.setSelected(selected - 1);
        }
        gameMenu.update();
    }
}
