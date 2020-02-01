package com.gadarts.war.menu.console.commands;

public enum ConsoleCommands {
	PROFILER(new ProfilerCommand()),
	BORDERS(new BordersCommand());

	private final ConsoleCommand command;

	ConsoleCommands(ConsoleCommand command) {
		this.command = command;
	}

	public ConsoleCommand getCommand() {
		return command;
	}
}
