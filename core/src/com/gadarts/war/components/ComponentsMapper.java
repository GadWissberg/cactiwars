package com.gadarts.war.components;

import com.badlogic.ashley.core.ComponentMapper;
import com.gadarts.war.components.character.CharacterComponent;
import com.gadarts.war.components.model.ModelInstanceComponent;
import com.gadarts.war.components.physics.PhysicsComponent;

public class ComponentsMapper {
    public static final ComponentMapper<CameraComponent> camera = ComponentMapper.getFor(CameraComponent.class);
    public static final ComponentMapper<ModelInstanceComponent> modelInstance = ComponentMapper.getFor(ModelInstanceComponent.class);
    public static final ComponentMapper<PhysicsComponent> physics = ComponentMapper.getFor(PhysicsComponent.class);
    public static final ComponentMapper<CharacterComponent> characters = ComponentMapper.getFor(CharacterComponent.class);
    public static final ComponentMapper<GroundComponent> ground = ComponentMapper.getFor(GroundComponent.class);
    public static final ComponentMapper<EnvironmentObjectComponent> environmentObject = ComponentMapper.getFor(EnvironmentObjectComponent.class);

}
