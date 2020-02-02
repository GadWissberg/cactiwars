package com.gadarts.war.menu.console.commands;

import java.util.Arrays;
import java.util.Optional;

public enum Commands {
	PROFILER(new ProfilerCommand()),
	BORDERS(new BordersCommand()),
	SKIP_DRAWING_MODE(new SkipDrawingModeCommand(), "skip_draw");

	private final ConsoleCommand command;
	private final String alias;

	Commands(ConsoleCommand command) {
		this(command, null);
	}

	Commands(ConsoleCommand command, String alias) {
		this.alias = alias;
		this.command = command;
	}

	public static Commands findCommandByNameOrAlias(String input) {
		Optional<Commands> result;
		try {
			result = Optional.of(valueOf(input));
		} catch (IllegalArgumentException e) {
			Commands[] values = values();
			result = Arrays.stream(values).filter(command ->
					Optional.ofNullable(command.getAlias()).isPresent() &&
							command.getAlias().equalsIgnoreCase(input)).findFirst();
			if (!result.isPresent()) {
				throw new IllegalArgumentException();
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
}
