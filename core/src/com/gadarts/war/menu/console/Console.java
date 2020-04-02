package com.gadarts.war.menu.console;

import com.gadarts.war.menu.console.commands.CommandParameter;
import com.gadarts.war.menu.console.commands.Commands;
import com.gadarts.war.menu.console.commands.ConsoleCommandResult;

public interface Console {
	void insertNewLog(String text, boolean logTime);

	ConsoleCommandResult notifyCommandExecution(Commands command);

	ConsoleCommandResult notifyCommandExecution(Commands command, CommandParameter parameter);

	void activate();

	void deactivate();

	boolean isActive();
}
