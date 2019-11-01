package com.gadarts.war;

public final class GameC {

    public final class Files {
        public static final String ASSETS_PATH = "core/assets/";
        public static final String TEXTURES_FOLDER_NAME = ASSETS_PATH + "textures";
        public static final String MODELS_FOLDER_NAME = ASSETS_PATH + "models";
        public static final String SOUNDS_FOLDER_NAME = ASSETS_PATH + "sounds";
        public static final String SOUND_FORMAT = "wav";
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

    public final class Artillery {
        public static final float MAX_FRONT_SPEED = 2;
        public static final float MAX_REVERSE_SPEED = 1;
        public static final float REVERSE_ACCELERATION = 0.1f;
        public static final float ACCELERATION = 0.2f;
        public static final float DECELERATION = 0.05f;
        public static final float ROTATION = 6;

    }


}
