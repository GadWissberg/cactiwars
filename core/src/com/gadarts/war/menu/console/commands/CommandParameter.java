package com.gadarts.war.menu.console.commands;

import com.gadarts.war.menu.console.Console;

public abstract class CommandParameter {
	private boolean parameterValue;

	public boolean getParameterValue() {
		return parameterValue;
	}

	protected void setParameterValue(boolean result) {
		this.parameterValue = result;
	}

	public abstract String getAlias();

	public abstract void run(String value, Console console);
}
