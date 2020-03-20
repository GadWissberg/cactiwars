package com.gadarts.war.systems.player.input.definitions.down;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.shared.definitions.WeaponDefinition;
import com.gadarts.war.InGameScreen;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.physics.MotionState;
import com.gadarts.war.systems.physics.PhysicsSystem;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

import static com.gadarts.war.systems.physics.PhysicsSystem.auxMatrix;
import static com.gadarts.war.systems.physics.PhysicsSystem.auxVector3_1;

public class Ctrl implements InputEvent {

	@Override
	public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers, InGameScreen parentScreen) {
		if (!ComponentsMapper.players.has(entity)) return false;
		MotionState motionState = ComponentsMapper.physics.get(entity).getMotionState();
		Vector3 worldTranslation = motionState.getWorldTranslation(auxVector3_1);
		WeaponDefinition weapon = ComponentsMapper.characters.get(entity).getCharacterDefinition().getWeapon();
		motionState.getWorldTransform(PhysicsSystem.auxMatrix);
		parentScreen.getActorFactory().createBullet(auxMatrix, worldTranslation.add(0, 1, 0), weapon);
		return true;
	}
}
