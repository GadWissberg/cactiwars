package com.gadarts.war.menu.console.commands;

import com.gadarts.war.menu.console.Console;

public abstract class CommandParameter {
	private final String description;
	private final String alias;
	private boolean parameterValue;

	public CommandParameter(String description, String alias) {
		this.description = description;
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public String getDescription() {
		return description;
	}

	public boolean getParameterValue() {
		return parameterValue;
	}

	protected void setParameterValue(boolean result) {
		this.parameterValue = result;
	}

	public abstract void run(String value, Console console);
}
