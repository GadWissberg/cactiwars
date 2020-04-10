package com.gadarts.war.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.List;

public class LightEmitterComponent implements Component, Pool.Poolable {
	private final List<Entity> sourceLights = new ArrayList<>();

	@Override
	public void reset() {
		sourceLights.clear();
	}

	public List<Entity> getSourceLights() {
		return sourceLights;
	}

	public void addSourceLight(Entity light) {
		sourceLights.add(light);
	}

}
