package com.gadarts.war.menu.input.definitions;

import com.badlogic.gdx.audio.Sound;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameScreen;
import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.sound.SFX;

public class MenuArrowUpDefinition implements MenuInputDefinition {
    @Override
    public void execute(GameMenu gameMenu, GameScreen parentScreen) {
        parentScreen.getSoundPlayer().play(GameAssetManager.getInstance().get(SFX.MENU_MOVE.getFileName(), Sound.class));
        int selected = gameMenu.getSelected();
        if (selected == 0) {
            gameMenu.setSelected(gameMenu.getOptionsTable().getChildren().size - 1);
        } else {
            gameMenu.setSelected(selected - 1);
        }
        gameMenu.update();
    }
}
