package com.gadarts.war.menu.console.commands;

import com.gadarts.war.menu.console.Console;

import java.util.Map;

public abstract class ConsoleCommand {
	public CommandResult run(Console console, Map<String, String> parameters) {
		return console.notifyCommandExecution(getCommandEnumValue());
	}

	protected abstract Commands getCommandEnumValue();

}
