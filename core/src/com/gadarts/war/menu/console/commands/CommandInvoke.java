package com.gadarts.war.menu.console.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandInvoke {
	private final CommandsList command;
	private Map<String, String> parameters = new HashMap<>();

	public CommandInvoke(CommandsList command) {
		this.command = command;
	}

	public CommandsList getCommand() {
		return command;
	}

	public void addParameter(String parameter, String value) {
		parameters.put(parameter, value);
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
}
