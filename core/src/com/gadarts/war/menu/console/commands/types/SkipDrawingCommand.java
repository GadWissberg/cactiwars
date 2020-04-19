package com.gadarts.war.menu.console.commands.types;

import com.gadarts.shared.console.CommandParameter;
import com.gadarts.shared.console.Console;
import com.gadarts.war.menu.console.commands.CommandsList;
import com.gadarts.war.menu.console.commands.ConsoleCommandImpl;


public class SkipDrawingCommand extends ConsoleCommandImpl {
	private static final String SKIP_DRAWING = "%s objects drawing are skipped.";
	private static final String DONT_SKIP_DRAWING = "%s objects drawing are not skipped.";


	@Override
	protected CommandsList getCommandEnumValue() {
		return CommandsList.SKIP_DRAWING;
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


		@Override
		public void run(String value, Console console) {
			defineParameterValue(value, console, SKIP_DRAWING, DONT_SKIP_DRAWING);
			console.notifyCommandExecution(CommandsList.SKIP_DRAWING, this);
		}
	}
}
