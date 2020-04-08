package com.gadarts.war.menu.console;

import com.gadarts.shared.console.CommandParameter;
import com.gadarts.shared.console.Commands;
import com.gadarts.shared.console.ConsoleCommandResult;

public interface ConsoleEventsSubscriber {
	void onConsoleActivated();

	boolean onCommandRun(Commands command, ConsoleCommandResult consoleCommandResult);

	boolean onCommandRun(Commands command, ConsoleCommandResult consoleCommandResult, CommandParameter parameter);

	void onConsoleDeactivated();
}
