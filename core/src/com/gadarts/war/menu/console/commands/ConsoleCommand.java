package com.gadarts.war.menu.console.commands;

public abstract class ConsoleCommand {
	public CommandResult run(Console console, ConsoleCommands command) {
		return console.notifyCommandExecution(command);
	}

	public abstract boolean run(Console subscriber);
}
