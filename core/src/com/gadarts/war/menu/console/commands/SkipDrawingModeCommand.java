package com.gadarts.war.menu.console.commands;

import com.gadarts.war.menu.console.Console;
import com.gadarts.war.systems.render.RenderSettings;

public class SkipDrawingModeCommand extends ConsoleCommand {
	private static final String SKIP_DRAWING_MODE = "Skip Drawing Mode is %s.";

	@Override
	public CommandResult run(Console console) {
		CommandResult result = super.run(console);
		RenderSettings.SKIP_DRAWING_MODE = !RenderSettings.SKIP_DRAWING_MODE;
		String msg = String.format(SKIP_DRAWING_MODE, RenderSettings.SKIP_DRAWING_MODE ? "activated" : "deactivated");
		console.insertNewLog(msg, false);
		return result;
	}

	@Override
	protected Commands getCommandEnumValue() {
		return Commands.SKIP_DRAWING_MODE;
	}
}
