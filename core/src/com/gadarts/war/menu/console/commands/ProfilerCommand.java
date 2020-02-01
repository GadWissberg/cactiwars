package com.gadarts.war.menu.console.commands;

public class ProfilerCommand extends ConsoleCommand {
	public static final String PROFILING_ACTIVATED = "Profiling info is displayed.";
	public static final String PROFILING_DEACTIVATED = "Profiling info is hidden.";

	@Override
	public boolean run(Console console) {
		CommandResult result = super.run(console, ConsoleCommands.PROFILER);
		console.insertNewLog(result.getMessage(), false);
		return true;
	}
}
