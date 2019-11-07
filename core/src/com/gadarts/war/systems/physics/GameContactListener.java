package com.gadarts.war.systems.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gadarts.war.components.ComponentsMapper;
import com.gadarts.war.components.physics.PhysicsComponent;
import com.gadarts.war.level.GroundChildShape;
import com.gadarts.war.sound.SFX;
import com.gadarts.war.sound.SoundPlayer;

import java.util.ArrayList;
import java.util.List;

public class GameContactListener extends ContactListener {
    private final SoundPlayer soundPlayer;
    private List<GameContactListenerEventsSubscriber> subscribers = new ArrayList<>();

    public GameContactListener(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void onContactStarted(btCollisionObject colObj0, boolean match0, btCollisionObject colObj1, boolean match1) {
        if (match0) {
            checkCollisionOnContactStarted(colObj0, colObj1);
        }
        if (match1) {
            checkCollisionOnContactStarted(colObj1, colObj0);
        }
    }

//    @Override
//    public boolean onContactAdded(btCollisionObject colObj0, int partId0, int index0, boolean match0,
//                                  btCollisionObject colObj1, int partId1, int index1, boolean match1) {
//        if (match0) {
//            checkCollisionOnContactAdded(colObj0, colObj1, index0, index1);
//        }
//        if (match1) {
//            checkCollisionOnContactAdded(colObj1, colObj0, index1, index0);
//        }
//        return match0 || match1;
//    }

    private void checkCollisionOnContactAdded(btCollisionObject colObj0, btCollisionObject colObj1, int id0, int id1) {
        Entity entity0 = (Entity) colObj0.userData;
        Entity entity1 = (Entity) colObj1.userData;
        if (entity0 == null || entity1 == null) return;
        if (ComponentsMapper.characters.has(entity0)) {
            if (colObj1.userData != null) {
                if (ComponentsMapper.ground.has(entity1)) {
                    onContactAddedForCharacterWithGround(entity0, entity1, id1);
                }
            }
        }
    }

    private void onContactAddedForCharacterWithGround(Entity character, Entity ground, int childShapeIndex) {
        ComponentsMapper.characters.get(character);
        btCompoundShape compound = (btCompoundShape) ComponentsMapper.physics.get(ground).getBody().getCollisionShape();
        GroundChildShape child = (GroundChildShape) compound.getChildShape(childShapeIndex);
        Gdx.app.log("!", ""+child.getTest());
        ComponentsMapper.ground.get(ground).getFrictionMapping();
    }

    private boolean checkCollisionOnContactStarted(btCollisionObject filterMatched, btCollisionObject match) {
        boolean result = false;
        Entity entity0 = (Entity) filterMatched.userData;
        Entity entity1 = (Entity) match.userData;
        if (entity0 == null || entity1 == null) return false;
        if (ComponentsMapper.characters.has(entity0)) {
            if (match.userData != null) {
                if (ComponentsMapper.ground.has(entity1)) {
                    onContactStartedForCharacterWithGround(entity0, entity1);
                    result = true;
                } else if (ComponentsMapper.environmentObject.has(entity1)) {
                    onCharacterWithEnvironmentObject(entity0, entity1);
                    result = true;
                }
            }
        } else if (ComponentsMapper.environmentObject.has(entity0)) {
            if (ComponentsMapper.ground.has(entity1)) {
                onEnvironmentObjectWithGround(entity1, entity0);
                result = true;
            }
        }
        return result;
    }

    private void onEnvironmentObjectWithGround(Entity groundEntity, Entity envObjectEntity) {
        float linearSpeed = ComponentsMapper.physics.get(envObjectEntity).getBody().getLinearVelocity().len2();
        if (linearSpeed > 5) {
            soundPlayer.playRandomByDefinitions(SFX.LIGHT_METAL_CRASH_1, SFX.LIGHT_METAL_CRASH_2);
        }
    }

    private void onContactStartedForCharacterWithGround(Entity characterEntity, Entity groundEntity) {
        PhysicsComponent characterPhysicsComponent = ComponentsMapper.physics.get(characterEntity);
        float linearV = characterPhysicsComponent.getBody().getLinearVelocity().len2();
        if (linearV > ComponentsMapper.characters.get(characterEntity).getGroundCrashThreshold())
            soundPlayer.playRandomByDefinitions(SFX.HEAVY_METAL_CRASH_1, SFX.HEAVY_METAL_CRASH_2);
    }

    private void onCharacterWithEnvironmentObject(Entity characterEntity, Entity envEntity) {
        PhysicsComponent characterPhysicsComponent = ComponentsMapper.physics.get(characterEntity);
        btRigidBody characterBody = characterPhysicsComponent.getBody();
        PhysicsComponent envPhysicsComponent = ComponentsMapper.physics.get(envEntity);
        if (characterBody.getLinearVelocity().len2() * characterPhysicsComponent.getMass() > envPhysicsComponent.getMass() * 159) {
            if (envPhysicsComponent.isStatic()) {
                subscribers.forEach(sub -> sub.onStaticEnvironmentObjectHardCollision(envEntity));
            }
            soundPlayer.playRandomByDefinitions(SFX.HEAVY_METAL_CRASH_1, SFX.HEAVY_METAL_CRASH_2);
        }
    }

    public void subscribe(GameContactListenerEventsSubscriber subscriber) {
        if (subscribers.contains(subscriber)) return;
        subscribers.add(subscriber);
    }
}
