package com.gadarts.war.menu;

public enum GameMenuOptions {
    NEW("New Game"),
    SAVE("Save Game"),
    LOAD("Load Game"),
    INFO("Info"),
    QUIT("Quit");

    private final String label;

    GameMenuOptions(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
