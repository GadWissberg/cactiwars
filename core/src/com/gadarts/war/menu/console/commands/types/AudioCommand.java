package com.gadarts.war.menu.console.commands.types;

import com.gadarts.shared.console.CommandParameter;
import com.gadarts.shared.console.Console;
import com.gadarts.war.menu.console.commands.CommandsList;
import com.gadarts.war.menu.console.commands.ConsoleCommandImpl;

public class AudioCommand extends ConsoleCommandImpl {
	private static final String AUDIO_ENABLED = "%s audio enabled.";
	private static final String AUDIO_DISABLED = "%s audio disabled.";

	@Override
	protected CommandsList getCommandEnumValue() {
		return CommandsList.AUDIO;
	}

	public static class AllParameter extends AudioParameter {
		public static final String DESCRIPTION = "Toggles over-all audio.";
		public static final String ALIAS = "all";

		public AllParameter() {
			super(DESCRIPTION, ALIAS);
		}

	}

	public static class AmbianceParameter extends AudioParameter {
		public static final String DESCRIPTION = "Toggles ambiance audio.";
		public static final String ALIAS = "ambiance";

		public AmbianceParameter() {
			super(DESCRIPTION, ALIAS);
		}
	}

	public static class MenuParameter extends AudioParameter {
		public static final String DESCRIPTION = "Toggles menu audio.";
		public static final String ALIAS = "menu";

		public MenuParameter() {
			super(DESCRIPTION, ALIAS);
		}
	}

	public static class WeaponryParameter extends AudioParameter {
		public static final String DESCRIPTION = "Toggles weaponry audio.";
		public static final String ALIAS = "weaponry";

		public WeaponryParameter() {
			super(DESCRIPTION, ALIAS);
		}
	}

	public static class MiscParameter extends AudioParameter {
		public static final String DESCRIPTION = "Toggles misc audio.";
		public static final String ALIAS = "misc";

		public MiscParameter() {
			super(DESCRIPTION, ALIAS);
		}
	}

	private abstract static class AudioParameter extends CommandParameter {
		public AudioParameter(String description, String alias) {
			super(description, alias);
		}

		@Override
		public void run(String value, Console console) {
			defineParameterValue(value, console, AUDIO_ENABLED, AUDIO_DISABLED);
			console.notifyCommandExecution(CommandsList.AUDIO, this);
		}
	}
}
