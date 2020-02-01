package com.gadarts.war.menu.console.commands;

import com.gadarts.war.menu.console.Console;

public class BordersCommand extends ConsoleCommand {
	public static final String BORDERS_ACTIVATED = "Debugging borders are displayed.";
	public static final String BORDERS_DEACTIVATED = "Debugging borders are hidden.";

	@Override
	public boolean run(Console console) {
		CommandResult result = super.run(console, ConsoleCommands.BORDERS);
		console.insertNewLog(result.getMessage(), false);
		return true;
	}
}
