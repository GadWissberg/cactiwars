package com.gadarts.war.menu.console;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;

public class ConsoleInputHistoryHandler {
	private Stage stage;
	private Array<String> inputHistory = new Array<>();
	private int current;

	public void applyInput(String inputCommand) {
		inputHistory.insert(inputHistory.size, inputCommand);
		current = inputHistory.size;
	}

	public void onKeyDown(int keycode) {
		if (keycode == Input.Keys.DOWN) {
			current = Math.min(inputHistory.size - 1, current + 1);
			updateInputByHistory();
		} else if (keycode == Input.Keys.UP) {
			current = Math.max(0, current - 1);
			updateInputByHistory();
		}
	}

	private void updateInputByHistory() {
		TextField input = stage.getRoot().findActor(ConsoleImpl.INPUT_FIELD_NAME);
		input.setText(inputHistory.get(current));
		input.setCursorPosition(input.getText().length());
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
