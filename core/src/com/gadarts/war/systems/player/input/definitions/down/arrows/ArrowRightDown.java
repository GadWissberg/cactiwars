package com.gadarts.war.systems.player.input.definitions.down.arrows;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.character.CharacterComponent;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;

import java.util.List;

public class ArrowRightDown extends ArrowPressedEvent{
    @Override
    public boolean run(Entity entity, List<PlayerSystemEventsSubscriber> subscribers) {
        super.run(entity, subscribers);
        CharacterComponent characterComponent = ComponentsMapper.characters.get(entity);
        characterComponent.setRotation(-characterComponent.getRotationDefinition());
        return true;
    }
}
