package com.gadarts.war.menu.console.commands.types;

import com.gadarts.war.menu.console.Console;
import com.gadarts.war.menu.console.commands.CommandParameter;
import com.gadarts.war.menu.console.commands.CommandResult;
import com.gadarts.war.menu.console.commands.Commands;
import com.gadarts.war.menu.console.commands.ConsoleCommand;
import com.gadarts.war.systems.render.RenderSettings;

import java.util.Arrays;
import java.util.Map;

public class SkipDrawingCommand extends ConsoleCommand {
	private static final String SKIP_DRAWING = "%s objects drawing are skipped.";
	private static final String DONT_SKIP_DRAWING = "%s objects drawing are not skipped.";

	@Override
	public CommandResult run(Console console, Map<String, String> parameters) {
		CommandResult result = super.run(console, parameters);
		parameters.forEach((key, value) -> Arrays.stream(getCommandEnumValue().getParameters()).forEach(parameter -> {
			if (key.equalsIgnoreCase(parameter.getAlias())) {
				parameter.run(value, console);
			}
		}));
		return result;
	}

	@Override
	protected Commands getCommandEnumValue() {
		return Commands.SKIP_DRAWING;
	}

	public static class GroundParameter extends SkipDrawingParameter {
		public GroundParameter(String ground) {
			super(ground);
		}

		@Override
		public boolean run(String value, Console console) {
			boolean result = parseSkipDrawingParameter(value, console);
			RenderSettings.SKIP_GROUND_DRAWING = result;
			RenderSettings.SKIP_DRAWING_SURROUNDING_TERRAIN = result;
			return result;
		}

	}

	public static class CharactersParameter extends SkipDrawingParameter {
		public CharactersParameter(String alias) {
			super(alias);
		}

		@Override
		public boolean run(String value, Console console) {
			boolean result = parseSkipDrawingParameter(value, console);
			RenderSettings.SKIP_CHARACTER_DRAWING = result;
			return result;
		}
	}

	public static class EnvironmentParameter extends SkipDrawingParameter {
		public EnvironmentParameter(String alias) {
			super(alias);
		}

		@Override
		public boolean run(String value, Console console) {
			boolean result = parseSkipDrawingParameter(value, console);
			RenderSettings.SKIP_ENV_OBJECT_DRAWING = result;
			return result;
		}
	}

	public static class ShadowsParameter extends SkipDrawingParameter {
		public ShadowsParameter(String alias) {
			super(alias);
		}

		@Override
		public boolean run(String value, Console console) {
			boolean result = parseSkipDrawingParameter(value, console);
			RenderSettings.SKIP_DRAW_SHADOWS = result;
			return result;
		}
	}

	private abstract static class SkipDrawingParameter extends CommandParameter {
		public SkipDrawingParameter(String alias) {
			super(alias);
		}

		protected boolean parseSkipDrawingParameter(String value, Console console) {
			boolean result;
			try {
				result = Integer.parseInt(value) == 1;
			} catch (NumberFormatException e) {
				result = false;
			}
			console.insertNewLog(String.format(result ? SKIP_DRAWING : DONT_SKIP_DRAWING, getAlias()), false);
			return result;
		}
	}
}
