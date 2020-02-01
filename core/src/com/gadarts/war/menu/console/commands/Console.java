package com.gadarts.war.menu.console.commands;

public interface Console {
	void insertNewLog(String text, boolean logTime);

	CommandResult notifyCommandExecution(ConsoleCommands command);
}
