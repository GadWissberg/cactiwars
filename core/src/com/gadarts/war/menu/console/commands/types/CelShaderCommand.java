package com.gadarts.war.menu.console.commands.types;

import com.gadarts.shared.console.Console;
import com.gadarts.shared.console.ConsoleCommandResult;
import com.gadarts.war.menu.console.commands.CommandsList;
import com.gadarts.war.menu.console.commands.ConsoleCommandImpl;

import java.util.Map;

public class CelShaderCommand extends ConsoleCommandImpl {
	@Override
	protected CommandsList getCommandEnumValue() {
		return CommandsList.CEL_SHADER;
	}

	@Override
	public ConsoleCommandResult run(Console console, Map<String, String> parameters) {
		ConsoleCommandResult result = super.run(console, parameters);
		return result;
	}
}
