package com.gadarts.war.components.character;

import com.badlogic.gdx.utils.Pool;
import com.gadarts.shared.definitions.CharacterAdditionalDefinition;

public class CharacterAdditional implements Pool.Poolable {
    private CharacterAdditionalDefinition definition;

    public CharacterAdditionalDefinition getDefinition() {
        return definition;
    }

    @Override
    public void reset() {

    }

    public void init(CharacterAdditionalDefinition additionalDefinition) {
        this.definition = additionalDefinition;
    }
}
