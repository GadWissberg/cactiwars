package com.gadarts.war.menu.console.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandInvoke {
	private final Commands command;
	private Map<String, String> parameters = new HashMap<>();

	public CommandInvoke(Commands command) {
		this.command = command;
	}

	public Commands getCommand() {
		return command;
	}

	public void addParameter(String parameter, String value) {
		parameters.put(parameter, value);
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
}
