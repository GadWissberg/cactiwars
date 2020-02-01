package com.gadarts.war.menu.console.commands;

public class CommandResult {
	private String message;
	private boolean result;

	public void clear() {
		message = null;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
}
