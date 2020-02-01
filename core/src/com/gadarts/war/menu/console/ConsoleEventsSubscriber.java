package com.gadarts.war.menu.console;

import com.gadarts.war.menu.console.commands.CommandResult;
import com.gadarts.war.menu.console.commands.ConsoleCommands;

public interface ConsoleEventsSubscriber {
	void onConsoleActivated();

	boolean onCommandRun(ConsoleCommands command, CommandResult commandResult);

	void onConsoleDeactivated();
}
