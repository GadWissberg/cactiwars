package com.gadarts.war.menu.console.commands.types;

import com.badlogic.gdx.utils.StringBuilder;
import com.gadarts.shared.console.Console;
import com.gadarts.shared.console.ConsoleCommandResult;
import com.gadarts.war.GameC;
import com.gadarts.war.menu.console.commands.CommandsList;
import com.gadarts.war.menu.console.commands.ConsoleCommandImpl;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class HelpCommand extends ConsoleCommandImpl {

	private static final String INTRO = "Welcome to CactiWars v%s. The command pattern is '<COMMAND_NAME> " +
			"-<PARAMETER_1> <PARAMETER_1_VALUE>'. The following commands are available:\n%s";

	private static String output;

	@Override
	protected CommandsList getCommandEnumValue() {
		return CommandsList.HELP;
	}

	@Override
	public ConsoleCommandResult run(Console console, Map<String, String> parameters) {
		if (!Optional.ofNullable(output).isPresent()) {
			initializeMessage();
		}
		ConsoleCommandResult consoleCommandResult = new ConsoleCommandResult();
		consoleCommandResult.setMessage(output);
		return consoleCommandResult;
	}

	private void initializeMessage() {
		StringBuilder builder = new StringBuilder();
		Arrays.stream(CommandsList.values()).forEach(command -> {
			builder.append(" - ").append(command.name().toLowerCase());
			if (Optional.ofNullable(command.getAlias()).isPresent()) {
				builder.append(" (also '").append(command.getAlias()).append("')");
			}
			builder.append(": ").append(command.getDescription()).append("\n");
		});
		output = String.format(INTRO, GameC.General.VERSION, builder);
	}
}
