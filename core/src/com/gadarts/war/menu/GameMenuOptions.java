package com.gadarts.war.menu;

import com.badlogic.gdx.Gdx;

public enum GameMenuOptions {
    NEW("New Game", () -> {
    }),
    SAVE("Save Game", () -> {
    }),
    LOAD("Load Game", () -> {
    }),
    INFO("Info", () -> {
    }),
    QUIT("Quit", () -> {
        Gdx.app.exit();
    });

    private final String label;
    private final GameMenuOptionExecution execution;

    GameMenuOptions(String label, GameMenuOptionExecution gameMenuOptionExecution) {
        this.label = label;
        this.execution = gameMenuOptionExecution;
    }

    public String getLabel() {
        return label;
    }

    public void execute() {
        execution.execute();
    }
}
