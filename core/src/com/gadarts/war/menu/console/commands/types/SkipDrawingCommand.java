package com.gadarts.war.menu.console.commands.types;

import com.badlogic.gdx.utils.StringBuilder;
import com.gadarts.shared.console.CommandParameter;
import com.gadarts.shared.console.Console;
import com.gadarts.shared.console.ConsoleCommandResult;
import com.gadarts.war.menu.console.commands.CommandsImpl;
import com.gadarts.war.menu.console.commands.ConsoleCommand;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;


public class SkipDrawingCommand extends ConsoleCommand {
	public static final String NO_PARAMETERS = "No parameters were supplied. Please supply one or more of the following: %s";
	private static final String SKIP_DRAWING = "%s objects drawing are skipped.";
	private static final String DONT_SKIP_DRAWING = "%s objects drawing are not skipped.";
	private StringBuilder stringBuilder = new StringBuilder();

	@Override
	public ConsoleCommandResult run(Console console, Map<String, String> parameters) {
		ConsoleCommandResult result = super.run(console, parameters);
		if (!parameters.isEmpty()) {
			parameters.forEach((key, value) -> Arrays.stream(getCommandEnumValue().getParameters()).forEach(parameter -> {
				if (key.equalsIgnoreCase(parameter.getAlias())) {
					parameter.run(value, console);
				}
			}));
		} else {
			printNoParameters(result);
		}
		return result;
	}

	private void printNoParameters(ConsoleCommandResult result) {
		int length = getCommandEnumValue().getParameters().length;
		IntStream.range(0, length).forEach(i -> {
			stringBuilder.append(getCommandEnumValue().getParameters()[i].getAlias());
			if (i < length - 1) {
				stringBuilder.append(", ");
			}
		});
		result.setMessage(String.format(NO_PARAMETERS, stringBuilder));
	}

	@Override
	protected CommandsImpl getCommandEnumValue() {
		return CommandsImpl.SKIP_DRAWING;
	}

	public static class GroundParameter extends SkipDrawingParameter {

		public static final String ALIAS = "ground";
		private static final String DESCRIPTION = "0 - Draws ground. 1 - Skips.";

		public GroundParameter() {
			super(DESCRIPTION, ALIAS);
		}

	}

	public static class CharactersParameter extends SkipDrawingParameter {

		public static final String ALIAS = "characters";
		private static final String DESCRIPTION = "0 - Draws characters. 1 - Skips.";

		public CharactersParameter() {
			super(DESCRIPTION, ALIAS);
		}
	}

	public static class EnvironmentParameter extends SkipDrawingParameter {

		public static final String ALIAS = "environment";
		private static final String DESCRIPTION = "0 - Draws environment objects. 1 - Skips.";

		public EnvironmentParameter() {
			super(DESCRIPTION, ALIAS);
		}


	}

	public static class ShadowsParameter extends SkipDrawingParameter {

		public static final String ALIAS = "shadows";
		private static final String DESCRIPTION = "0 - Draws shadows. 1 - Skips.";

		public ShadowsParameter() {
			super(DESCRIPTION, ALIAS);
		}

	}

	public abstract static class SkipDrawingParameter extends CommandParameter {


		public SkipDrawingParameter(String description, String alias) {
			super(description, alias);
		}

		protected boolean defineSkipDrawingParameter(String value, Console console) {
			boolean result;
			try {
				result = Integer.parseInt(value) == 1;
			} catch (NumberFormatException e) {
				result = false;
			}
			console.insertNewLog(String.format(result ? SKIP_DRAWING : DONT_SKIP_DRAWING, getAlias()), false);
			setParameterValue(result);
			return result;
		}

		@Override
		public void run(String value, Console console) {
			defineSkipDrawingParameter(value, console);
			console.notifyCommandExecution(CommandsImpl.SKIP_DRAWING, this);
		}
	}
}
