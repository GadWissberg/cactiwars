package com.gadarts.war.menu;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class GameMenuOption extends Label {
    private final GameMenuOptions optionDefinition;

    public GameMenuOption(GameMenuOptions optionDefinition, LabelStyle style) {
        super(optionDefinition.getLabel(), style);
        this.optionDefinition = optionDefinition;
    }

    public GameMenuOptions getOptionDefinition() {
        return optionDefinition;
    }
}
