package com.gadarts.war.menu.console.commands.types;

import com.gadarts.shared.console.Console;
import com.gadarts.shared.console.ConsoleCommandResult;
import com.gadarts.war.menu.console.commands.CommandsImpl;
import com.gadarts.war.menu.console.commands.ConsoleCommand;

import java.util.Map;

public class CelShaderCommand extends ConsoleCommand {
	@Override
	protected CommandsImpl getCommandEnumValue() {
		return CommandsImpl.CEL_SHADER;
	}

	@Override
	public ConsoleCommandResult run(Console console, Map<String, String> parameters) {
		ConsoleCommandResult result = super.run(console, parameters);
		return result;
	}
}
