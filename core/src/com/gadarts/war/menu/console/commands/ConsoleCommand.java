package com.gadarts.war.menu.console.commands;

import com.gadarts.war.menu.console.Console;

public abstract class ConsoleCommand {
	public CommandResult run(Console console, ConsoleCommands command) {
		return console.notifyCommandExecution(command);
	}

	public abstract boolean run(Console subscriber);
}
