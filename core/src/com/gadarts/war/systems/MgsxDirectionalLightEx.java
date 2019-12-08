package com.gadarts.war.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;

public class MgsxDirectionalLightEx extends DirectionalLight {
    /**
     * base color clamped
     */
    public final Color baseColor = new Color(Color.WHITE);

    /**
     * light intensity in lux (lm/m2)
     */
    public float intensity = 1f;

    @Override
    public DirectionalLight set(final DirectionalLight copyFrom) {
        if (copyFrom instanceof MgsxDirectionalLightEx) {
            return set(((MgsxDirectionalLightEx) copyFrom).baseColor, copyFrom.direction, ((MgsxDirectionalLightEx) copyFrom).intensity);
        } else {
            return set(copyFrom.color, copyFrom.direction, 1f);
        }
    }

    public MgsxDirectionalLightEx set(Color baseColor, Vector3 direction, float intensity) {
        this.intensity = intensity;
        this.baseColor.set(baseColor);
        this.direction.set(direction);
        updateColor();
        return this;
    }

    public void updateColor() {
        this.color.r = baseColor.r * intensity;
        this.color.g = baseColor.g * intensity;
        this.color.b = baseColor.b * intensity;
    }

    @Override
    public boolean equals(DirectionalLight other) {
        return (other instanceof MgsxDirectionalLightEx) ? equals((MgsxDirectionalLightEx) other) : false;
    }

    public boolean equals(MgsxDirectionalLightEx other) {
        return (other != null) && ((other == this) || ((baseColor.equals(other.baseColor) && Float.compare(intensity, other.intensity) == 0 && direction.equals(other.direction))));
    }
}
