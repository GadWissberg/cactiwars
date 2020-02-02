package com.gadarts.war.menu.console.commands;

import com.gadarts.war.menu.console.Console;

public abstract class ConsoleCommand {
	public CommandResult run(Console console) {
		return console.notifyCommandExecution(getCommandEnumValue());
	}

	protected abstract Commands getCommandEnumValue();

}
