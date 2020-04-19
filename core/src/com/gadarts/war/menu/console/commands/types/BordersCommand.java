package com.gadarts.war.menu.console.commands.types;

import com.gadarts.shared.console.Console;
import com.gadarts.shared.console.ConsoleCommandResult;
import com.gadarts.war.menu.console.commands.CommandsList;
import com.gadarts.war.menu.console.commands.ConsoleCommandImpl;

import java.util.Map;

public class BordersCommand extends ConsoleCommandImpl {
	public static final String BORDERS_ACTIVATED = "Debugging borders are displayed.";
	public static final String BORDERS_DEACTIVATED = "Debugging borders are hidden.";

	@Override
	public ConsoleCommandResult run(Console console, Map<String, String> parameters) {
		ConsoleCommandResult result = super.run(console, parameters);
		return result;
	}

	@Override
	protected CommandsList getCommandEnumValue() {
		return CommandsList.BORDERS;
	}
}
