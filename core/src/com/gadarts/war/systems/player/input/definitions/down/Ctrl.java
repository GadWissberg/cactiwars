package com.gadarts.war.systems.player.input.definitions.down;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.shared.definitions.WeaponDefinition;
import com.gadarts.war.InGameScreen;
import com.gadarts.war.components.CameraComponent;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.sound.SFX;
import com.gadarts.war.systems.CharacterSystem;
import com.gadarts.war.systems.player.PlayerSystemEventsSubscriber;
import com.gadarts.war.systems.player.input.InputEvent;

import java.util.List;

import static com.gadarts.war.systems.physics.PhysicsSystem.auxVector3_1;

public class Ctrl implements InputEvent {

	@Override
	public boolean execute(Entity entity, List<PlayerSystemEventsSubscriber> subscribers, InGameScreen parentScreen) {
		if (!ComponentsMapper.players.has(entity)) return false;
		Vector2 direction = ComponentsMapper.characters.get(entity).getDirection(CharacterSystem.auxVector2_1);
		Vector3 worldTranslation = ComponentsMapper.physics.get(entity).getMotionState().getWorldTranslation(auxVector3_1);
		WeaponDefinition weapon = ComponentsMapper.characters.get(entity).getCharacterDefinition().getWeapon();
		parentScreen.getActorFactory().createBullet(direction, worldTranslation, weapon);
		ComponentsMapper.physics.get(entity).getBody().applyTorqueImpulse(auxVector3_1.set(1, 0, 0).setLength(2000));
		CameraComponent cameraComponent = ComponentsMapper.camera.get(parentScreen.getEntitiesEngine().getEntitiesFor(Family.all(CameraComponent.class).get()).first());
		parentScreen.getSoundPlayer().play(SFX.HEAVY_METAL_CRASH_1, cameraComponent.getCamera(), auxVector3_1.set(10, 0, 0));
		return true;
	}
}
