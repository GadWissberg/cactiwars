package com.gadarts.war.menu.console.commands.types;

import com.gadarts.war.menu.console.Console;
import com.gadarts.war.menu.console.commands.Commands;
import com.gadarts.war.menu.console.commands.ConsoleCommand;
import com.gadarts.war.menu.console.commands.ConsoleCommandResult;

import java.util.Map;

public class BordersCommand extends ConsoleCommand {
	public static final String BORDERS_ACTIVATED = "Debugging borders are displayed.";
	public static final String BORDERS_DEACTIVATED = "Debugging borders are hidden.";

    @Override
    public ConsoleCommandResult run(Console console, Map<String, String> parameters) {
        ConsoleCommandResult result = super.run(console, parameters);
        return result;
    }

	@Override
	protected Commands getCommandEnumValue() {
		return Commands.BORDERS;
	}
}
