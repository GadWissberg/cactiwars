package com.gadarts.war.menu.console;

import com.gadarts.shared.GameException;

public class InputParsingFailureException extends GameException {
	public InputParsingFailureException(String message) {
		super(message);
	}
}
