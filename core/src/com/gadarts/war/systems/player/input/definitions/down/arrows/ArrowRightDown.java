package com.gadarts.war.systems.player.input.definitions.down.arrows;

import com.badlogic.ashley.core.Entity;
import com.gadarts.war.InGameScreen;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.character.CharacterComponent;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;

import java.util.List;

public class ArrowRightDown extends ArrowPressedEvent{
    @Override
    public boolean execute(Entity player, List<PlayerSystemEventsSubscriber> subscribers, InGameScreen parentScreen) {
        super.execute(player, subscribers, parentScreen);
        CharacterComponent characterComponent = ComponentsMapper.characters.get(player);
        characterComponent.setRotation(-characterComponent.getRotationDefinition());
        return true;
    }
}
