package com.gadarts.war;

import com.badlogic.gdx.Gdx;
import com.gadarts.war.screens.Screens;

public final class GameSettings {
    public static final boolean SPECTATOR = false;
    public static final int LOG_LEVEL = Gdx.app.LOG_DEBUG;
    public static final boolean FULL_SCREEN = false;
    public static final boolean SHOW_GL_PROFILING = true;
    public static final boolean SHOW_AXIS = false;
    public static final boolean ALLOW_SOUND = false;
    public static final boolean DRAW_COLLISION_SHAPES = false;
    public static final boolean MENU_ON_START = false;
    public static final boolean DRAW_TABLES_BORDERS = false;
    public static final boolean SKIP_DRAWING_MODE = false;
    public static final boolean SKIP_GROUND_DRAWING = true;
    public static final boolean SKIP_CHARACTER_DRAWING = false;
    public static final boolean SKIP_ENV_OBJECT_DRAWING = true;
    public static final boolean SKIP_DRAWING_SURROUNDING_TERRAIN = true;
    public static final boolean SKIP_DRAW_SHADOWS = true;
    public static final int FPS_TARGET = 60;
    public static final boolean CEL_SHADING = true;
    public static final boolean MUTE_AMB_SOUNDS = true;
    public static final boolean MUTE_CHARACTERS_SOUNDS = false;
    public static final Screens INITIAL_SCREEN = Screens.BATTLE;

}
