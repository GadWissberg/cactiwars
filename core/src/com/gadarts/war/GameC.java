package com.gadarts.war;

import com.badlogic.gdx.graphics.Color;

public final class GameC {
    public final class General {
        public static final String VERSION = "0.1";
        public static final String GAME = "Cactillery";

    }

    public static final class Menu {
        public static final String NAME_OPTIONS_TABLE = "options_table";

        public static final class CactusIcons {
            public static final String NAME_LEFT = "cactus_icon_left";
            public static final String NAME_RIGHT = "cactus_icon_right";

            public static final float MARGIN = 30f;
        }

    }

    public static final class ShadowMap {

        public static final int SIZE = 2048;
        public static final float VIEWPORT_SIZE = 30;

        public static final Color COLOR = new Color(0.1f, 0.1f, 0.1f, 1f);
    }


    public final class Files {
        public final class Font {
            public final class Size {

                public static final int BIG = 80;
                public static final int MED = 40;
            }

            public static final String FORMAT = "ttf";
            public static final String FOLDER_PATH = ASSETS_PATH + "fonts";
        }

        public final class Sound {

            public static final String FOLDER_PATH = ASSETS_PATH + "sounds";
            public static final String FORMAT = "wav";
        }

        public static final String ASSETS_PATH = "core/assets/";
        public static final String TEXTURES_FOLDER_NAME = ASSETS_PATH + "textures";
        public static final String MODELS_FOLDER_NAME = ASSETS_PATH + "models";
        public static final String MAIN_PAR_FILE = "core/assets/terralust.par";
    }

    public final class Camera {

        public static final float MAX_X_FRONT_OFFSET = 5;
        public static final float MAX_Z_FRONT_OFFSET = 8;
        public static final float MIN_Z_FRONT_OFFSET = 5;
        public static final float MAX_ZOOM_DISTANCE = 70;
        public static final float MIN_ZOOM_DISTANCE = 50;
    }

    public final class Profiler {
        public static final String FPS_STRING = "FPS: ";
        public static final String GL_CALL_STRING = "Calls: ";
        public static final String GL_DRAW_CALL_STRING = "Draw Calls: ";
        public static final String GL_SHADER_SWITCHES_STRING = "Shader Switches: ";
        public static final String GL_TEXTURE_BINDINGS_STRING = "Texture Bindings: ";
        public static final String GL_VERTEX_COUNT_STRING = "Vertex Count: ";
        public static final String BATCH_UI_CALLS_STRING = "UI Batch Calls: ";
        public static final String VISIBLE_OBJECTS_STRING = "Visible Objects: ";

    }

    public final class Tank {
        public static final float MAX_FRONT_SPEED = 2;
        public static final float MAX_REVERSE_SPEED = 1.5f;
        public static final float REVERSE_ACCELERATION = 0.1f;
        public static final float ACCELERATION = 0.2f;
        public static final float DECELERATION = 0.05f;
        public static final float ROTATION = 6;

    }


}
