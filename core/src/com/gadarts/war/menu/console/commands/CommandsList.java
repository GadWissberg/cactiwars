package com.gadarts.war.menu.console.commands;

import com.gadarts.shared.console.CommandParameter;
import com.gadarts.shared.console.Commands;
import com.gadarts.war.menu.console.ConsoleImpl;
import com.gadarts.war.menu.console.InputParsingFailureException;
import com.gadarts.war.menu.console.commands.types.*;

import java.util.Arrays;
import java.util.Optional;

public enum CommandsList implements Commands {
	PROFILER(new ProfilerCommand(),
			"Toggles profiler and GL operations stats."),
	AUDIO("sound", new AudioCommand(), "Toggles audio for given categories.",
			new AudioCommand.AllParameter(),
			new AudioCommand.AmbianceParameter(),
			new AudioCommand.MenuParameter(),
			new AudioCommand.WeaponryParameter(),
			new AudioCommand.MiscParameter()),
	BORDERS(new BordersCommand(), "Toggles UI components borders."),
	SKIP_DRAWING("skip_draw", new SkipDrawingCommand(),
			"Toggles drawing skipping mode for given categories.",
			new SkipDrawingCommand.GroundParameter(),
			new SkipDrawingCommand.CharactersParameter(),
			new SkipDrawingCommand.EnvironmentParameter(),
			new SkipDrawingCommand.ShadowsParameter()),
	CEL_SHADER("cel_shading", new CelShaderCommand(), "Toggles out-line effect."),
	HELP("?", new HelpCommand(), "Displays commands list.");

	public static final String DESCRIPTION_PARAMETERS = " Parameters:%s";
	private final ConsoleCommandImpl command;
	private final String alias;
	private final CommandParameter[] parameters;
	private String description;

	CommandsList(ConsoleCommandImpl command, String description) {
		this(null, command, description);
	}

	CommandsList(String alias, ConsoleCommandImpl command, String description, CommandParameter... parameters) {
		this.alias = alias;
		this.command = command;
		this.parameters = parameters;
		this.description = description;
		extendDescriptionWithParameters(parameters);
	}

	public static CommandsList findCommandByNameOrAlias(String input) throws InputParsingFailureException {
		Optional<CommandsList> result;
		try {
			result = Optional.of(valueOf(input));
		} catch (IllegalArgumentException e) {
			CommandsList[] values = values();
			result = Arrays.stream(values).filter(command ->
					Optional.ofNullable(command.getAlias()).isPresent() &&
							command.getAlias().equalsIgnoreCase(input)).findFirst();
			if (!result.isPresent()) {
				throw new InputParsingFailureException(String.format(ConsoleImpl.NOT_RECOGNIZED, input.toLowerCase()));
			}
		}
		return result.get();
	}

	private void extendDescriptionWithParameters(CommandParameter[] parameters) {
		if (parameters.length > 0) {
			StringBuilder stringBuilder = new StringBuilder();
			Arrays.stream(parameters).forEach(parameter -> stringBuilder
					.append("\n")
					.append("   * ")
					.append(parameter.getAlias())
					.append(": ")
					.append(parameter.getDescription()));
			this.description += String.format(DESCRIPTION_PARAMETERS, stringBuilder);
		}
	}

	public String getAlias() {
		return alias;
	}

	public ConsoleCommandImpl getCommandImpl() {
		return command;
	}

	public CommandParameter[] getParameters() {
		return parameters;
	}

	public String getDescription() {
		return description;
	}
}
