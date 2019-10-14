package com.gadarts.war.level;

import com.badlogic.ashley.core.PooledEngine;

public class Level {

    private LevelCreator levelCreator;
    private PooledEngine engine;

    public void init(PooledEngine engine) {
        this.engine = engine;
        levelCreator = new LevelCreator(engine);
        levelCreator.modelLevelGround();
        levelCreator.createLevelPhysics();
    }




}
