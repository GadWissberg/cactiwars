package com.gadarts.war.systems;

import com.badlogic.ashley.core.EntitySystem;

public abstract class GameEntitySystem extends EntitySystem {
	public abstract void onResize(int width, int height);
}
