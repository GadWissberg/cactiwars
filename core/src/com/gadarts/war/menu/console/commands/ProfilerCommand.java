package com.gadarts.war.menu.console.commands;

import com.gadarts.war.menu.console.Console;

public class ProfilerCommand extends ConsoleCommand {
	public static final String PROFILING_ACTIVATED = "Profiling info is displayed.";
	public static final String PROFILING_DEACTIVATED = "Profiling info is hidden.";

	@Override
	public CommandResult run(Console console) {
		CommandResult result = super.run(console);
		console.insertNewLog(result.getMessage(), false);
		return result;
	}

	@Override
	protected Commands getCommandEnumValue() {
		return Commands.PROFILER;
	}
}
