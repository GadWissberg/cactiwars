package com.gadarts.war.menu.console.commands.types;

import com.gadarts.war.menu.console.Console;
import com.gadarts.war.menu.console.commands.Commands;
import com.gadarts.war.menu.console.commands.ConsoleCommand;
import com.gadarts.war.menu.console.commands.ConsoleCommandResult;

import java.util.Map;

public class CelShaderCommand extends ConsoleCommand {
	@Override
	protected Commands getCommandEnumValue() {
		return Commands.CEL_SHADER;
	}

	@Override
	public ConsoleCommandResult run(Console console, Map<String, String> parameters) {
		ConsoleCommandResult result = super.run(console, parameters);
		return result;
	}
}
