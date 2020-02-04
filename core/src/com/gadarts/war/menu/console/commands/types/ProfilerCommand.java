package com.gadarts.war.menu.console.commands.types;

import com.gadarts.war.menu.console.Console;
import com.gadarts.war.menu.console.commands.CommandResult;
import com.gadarts.war.menu.console.commands.Commands;
import com.gadarts.war.menu.console.commands.ConsoleCommand;

import java.util.Map;

public class ProfilerCommand extends ConsoleCommand {
	public static final String PROFILING_ACTIVATED = "Profiling info is displayed.";
	public static final String PROFILING_DEACTIVATED = "Profiling info is hidden.";

	@Override
	public CommandResult run(Console console, Map<String, String> parameters) {
		CommandResult result = super.run(console, parameters);
		console.insertNewLog(result.getMessage(), false);
		return result;
	}

	@Override
	protected Commands getCommandEnumValue() {
		return Commands.PROFILER;
	}
}
