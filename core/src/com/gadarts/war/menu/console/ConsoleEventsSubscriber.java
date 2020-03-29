package com.gadarts.war.menu.console;

import com.gadarts.war.menu.console.commands.CommandParameter;
import com.gadarts.war.menu.console.commands.Commands;
import com.gadarts.war.menu.console.commands.ConsoleCommandResult;

public interface ConsoleEventsSubscriber {
	void onConsoleActivated();

	boolean onCommandRun(Commands command, ConsoleCommandResult consoleCommandResult);

	boolean onCommandRun(Commands command, ConsoleCommandResult consoleCommandResult, CommandParameter parameter);

	void onConsoleDeactivated();
}
