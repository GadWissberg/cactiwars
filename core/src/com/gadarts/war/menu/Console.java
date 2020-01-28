package com.gadarts.war.menu;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Console extends Table {
	public static final String NAME = "console";
	private static final float TRANSITION_DURATION = 2f;
	private boolean active;

	public Console() {
		setName(NAME);
		int width = Gdx.graphics.getWidth();
		Texture texture = new Texture(Gdx.files.getFileHandle("console.jpg", Files.FileType.Local));
		setBackground(new TextureRegionDrawable(texture));
		setSize(width, texture.getHeight());
		add().size(width, texture.getHeight());
		setPosition(0, Gdx.graphics.getHeight());
	}

	public void activate() {
		if (active) return;
		active = true;
		float amountY = -Gdx.graphics.getHeight() / 3f;
		addAction(Actions.moveBy(0, amountY, TRANSITION_DURATION, Interpolation.elastic));
		setVisible(true);
	}

	public void deactivate() {
		if (!active) return;
		active = false;
		float amountY = Gdx.graphics.getHeight() / 3f;
		MoveByAction move = Actions.moveBy(0, amountY, TRANSITION_DURATION, Interpolation.elastic);
		addAction(Actions.sequence(move, Actions.visible(false)));
	}

	public boolean isActive() {
		return active;
	}
}
