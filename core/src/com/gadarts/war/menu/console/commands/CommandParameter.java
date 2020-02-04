package com.gadarts.war.menu.console.commands;

import com.gadarts.war.menu.console.Console;

public abstract class CommandParameter {
	private final String alias;

	public CommandParameter(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public abstract boolean run(String value, Console console);
}
