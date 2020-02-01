package com.gadarts.war.menu;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class MenuOptionsTable extends Table {
	private final Table leftSide = new Table();
	private final Table rightSide = new Table();
	private Table options = new Table();

	public MenuOptionsTable() {
		add(leftSide);
		add(options);
		add(rightSide);
	}

    public void addOption(GameMenuOption menuOption) {
        options.add(menuOption).row();
        leftSide.add().height(menuOption.getPrefHeight()).row();
        rightSide.add().height(menuOption.getPrefHeight()).row();
    }

    void repositionCacti(int selected, Image leftCactusIcon, Image rightCactusIcon) {
        leftSide.getCell(leftCactusIcon).clearActor();
        rightSide.getCell(rightCactusIcon).clearActor();
        leftSide.getCells().get(selected).setActor(leftCactusIcon);
        rightSide.getCells().get(selected).setActor(rightCactusIcon);
    }

    public void addIndicators(Image leftCactusIcon, Image rightCactusIcon) {
        leftSide.getCells().get(0).setActor(leftCactusIcon);
        rightSide.getCells().get(0).setActor(rightCactusIcon);
    }

    public int getNumberOfOptions() {
        return options.getChildren().size;
    }
}
