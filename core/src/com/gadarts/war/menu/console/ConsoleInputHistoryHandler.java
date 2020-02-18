package com.gadarts.war.menu.console;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import java.util.Stack;

public class ConsoleInputHistoryHandler {
    private final Stage stage;
    private Stack<String> inputHistory = new Stack<>();
    private Stack<String> inputHistoryAux = new Stack<>();

    public ConsoleInputHistoryHandler(Stage stage) {
        this.stage = stage;
    }

    public void applyInput(String inputCommand) {
        if (!inputHistoryAux.empty()) inputHistoryAux.insertElementAt(inputCommand, 0);
        else inputHistory.push(inputCommand);
    }

    public void onKeyDown(int keycode) {
        if (keycode == Input.Keys.DOWN) {
            manipulateInputHistory(inputHistoryAux, inputHistory);
        } else if (keycode == Input.Keys.UP) {
            manipulateInputHistory(inputHistory, inputHistoryAux);
        }
    }

    private boolean manipulateInputHistory(Stack<String> takeFrom, Stack<String> putIn) {
        if (takeFrom.empty()) return true;
        TextField input = stage.getRoot().findActor(ConsoleImpl.INPUT_FIELD_NAME);
        String pop = takeFrom.pop();
        input.setText(pop);
        input.setCursorPosition(input.getText().length());
        putIn.push(pop);
        return true;
    }
}
