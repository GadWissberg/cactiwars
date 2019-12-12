package com.gadarts.war.components;

import com.badlogic.ashley.core.Entity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EnvironmentObjectComponentTest {

    private static final Entity LIGHT_ENTITY = new Entity();
    private EnvironmentObjectComponent component;

    @Before
    public void setUp() throws Exception {
        component = new EnvironmentObjectComponent();
    }

    @Test
    public void addSourceLight() {
        component.addSourceLight(LIGHT_ENTITY);
        assertTrue(component.getSourceLights().contains(LIGHT_ENTITY));
    }

    @Test
    public void reset() {
        component.addSourceLight(LIGHT_ENTITY);
        component.reset();
        assertTrue(component.getSourceLights().isEmpty());
    }
}