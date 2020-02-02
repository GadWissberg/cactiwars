package com.gadarts.war.menu.console;

import com.gadarts.war.menu.console.commands.CommandResult;
import com.gadarts.war.menu.console.commands.Commands;

public interface ConsoleEventsSubscriber {
	void onConsoleActivated();

	boolean onCommandRun(Commands command, CommandResult commandResult);

	void onConsoleDeactivated();
}
