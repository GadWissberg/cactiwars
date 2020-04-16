package com.gadarts.war;

import com.badlogic.gdx.graphics.Color;

public final class GameC {
	public static final float CHARACTER_ROTATION_MULTIPLIER_WITH_DT = 100;
	public static final float CHARACTER_MOVING_MULTIPLIER_WITH_DT = 10;

	public static final class Menu {
		public static final String LOGO_NAME = "logo";
		public static final String NAME_OPTIONS_TABLE = "options_table";

		public static final class MainMenu {
			public static final int MIN_SPEED = 1;
			public static final int MAX_SPEED = 3;
			public static final int NUMBER_OF_CACTI = 5;
			public static final String ATLAS_NAME = "menu";

			public static final class Camera {
				public static final float X = 5;
				public static final float Y = -10;
				public static final float Z = 10;

			}
		}

		public static final class BlurShaderKeys {
			public static final String BLUR_DIRECTION = "u_dir";
			public static final String BLUR_RESOLUTION = "u_resolution";
			public static final String BLUR_RADIUS = "u_radius";

		}

		public static final class CactusIcons {

			public static final float MARGIN = 30f;
			public static final String REGION_NAME = "cactus_icon";
		}

	}

	public static final class ShadowMap {

		public static final int SIZE = 2048;
		public static final float VIEWPORT_SIZE = 30;

		public static final Color COLOR = new Color(0.1f, 0.1f, 0.1f, 1f);
	}

	public final class General {
		public static final String VERSION = "0.1.2";
		public static final String GAME = "CactiWars";

	}

	public final class Files {
		public static final String TILES_ATLAS_ASSET_NAME = "ATL/tiles";
		public static final int ATLAS_SIZE = 1024;
		public static final String ASSETS_PATH = "./core/assets/";
		public static final String MODELS_FOLDER_NAME = ASSETS_PATH + "models";

		public final class Font {
			public static final String FORMAT = "ttf";
			public static final String FOLDER_PATH = ASSETS_PATH + "fonts";

			public final class Size {

				public static final int BIG = 80;
				public static final int MED = 40;
			}
		}

		public final class Sound {

			public static final String FOLDER_PATH = ASSETS_PATH + "sounds";
			public static final String FORMAT = "wav";
		}
	}

	public final class Camera {

		public static final float MAX_X_FRONT_OFFSET = 5;
		public static final float MAX_Z_FRONT_OFFSET = 10f;
		public static final float MIN_Z_FRONT_OFFSET = 5;
		public static final float MAX_ZOOM_DISTANCE = 150;
		public static final float MIN_ZOOM_DISTANCE = 50;
	}

	public final class Profiler {
		public static final String FPS_STRING = "FPS: ";
		public static final String GL_CALL_STRING = "Calls: ";
		public static final String GL_DRAW_CALL_STRING = "Draw Calls: ";
		public static final String GL_SHADER_SWITCHES_STRING = "Shader Switches: ";
		public static final String GL_TEXTURE_BINDINGS_STRING = "Texture Bindings: ";
		public static final String GL_VERTEX_COUNT_STRING = "Vertex Count: ";
		public static final String UI_BATCH_RENDER_CALLS_STRING = "UI Render Calls: ";
		public static final String VISIBLE_OBJECTS_STRING = "Visible Objects: ";

	}

	public final class Tank {
		public static final float MAX_FRONT_SPEED = 2;
		public static final float MAX_REVERSE_SPEED = 1.5f;
		public static final float REVERSE_ACCELERATION = 0.1f;
		public static final float ACCELERATION = 0.2f;
		public static final float DECELERATION = 0.05f;
		public static final float ROTATION = 0.1f;

	}


}
