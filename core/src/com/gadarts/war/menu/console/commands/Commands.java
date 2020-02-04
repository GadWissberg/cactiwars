package com.gadarts.war.menu.console.commands;

import com.gadarts.war.menu.console.ConsoleImpl;
import com.gadarts.war.menu.console.InputParsingFailureException;
import com.gadarts.war.menu.console.commands.types.BordersCommand;
import com.gadarts.war.menu.console.commands.types.ProfilerCommand;
import com.gadarts.war.menu.console.commands.types.SkipDrawingCommand;

import java.util.Arrays;
import java.util.Optional;

public enum Commands {
	PROFILER(new ProfilerCommand()),
	BORDERS(new BordersCommand()),
	SKIP_DRAWING("skip_draw", new SkipDrawingCommand(),
			new SkipDrawingCommand.GroundParameter("ground"),
			new SkipDrawingCommand.CharactersParameter("characters"),
			new SkipDrawingCommand.EnvironmentParameter("environment"),
			new SkipDrawingCommand.ShadowsParameter("shadows"));

	private final ConsoleCommand command;
	private final String alias;
	private final CommandParameter[] parameters;

	Commands(ConsoleCommand command) {
		this(null, command);
	}

	Commands(String alias, ConsoleCommand command, CommandParameter... parameters) {
		this.alias = alias;
		this.command = command;
		this.parameters = parameters;
	}

	public static Commands findCommandByNameOrAlias(String input) throws InputParsingFailureException {
		Optional<Commands> result;
		try {
			result = Optional.of(valueOf(input));
		} catch (IllegalArgumentException e) {
			Commands[] values = values();
			result = Arrays.stream(values).filter(command ->
					Optional.ofNullable(command.getAlias()).isPresent() &&
							command.getAlias().equalsIgnoreCase(input)).findFirst();
			if (!result.isPresent()) {
				throw new InputParsingFailureException(String.format(ConsoleImpl.NOT_RECOGNIZED, input));
			}
		}
		return result.get();
	}

	public String getAlias() {
		return alias;
	}

	public ConsoleCommand getCommandImpl() {
		return command;
	}

	public CommandParameter[] getParameters() {
		return parameters;
	}
}
