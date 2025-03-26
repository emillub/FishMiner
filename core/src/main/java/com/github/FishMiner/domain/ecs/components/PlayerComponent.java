package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;

public class PlayerComponent implements Component {
    public Entity hook;
    public Entity reel;
    public Entity sinker;

    public Vector3 hookAnchorPoint = new Vector3();
    public Vector3 reelAnchorPoint = new Vector3();
    public Vector3 sinkerAnchorPoint = new Vector3();

}
