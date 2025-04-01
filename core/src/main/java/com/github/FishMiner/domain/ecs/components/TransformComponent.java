package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TransformComponent implements Component {
    /**
     * pos.x and pos.y sets the center for the entity.
     * pos.z can usually remain 0 and is only used to easily separate background and foreground elements
     */
    public Vector3 pos = new Vector3();
    public Vector2 scale = new Vector2(1.0f, 1.0f);
    public float rotation = 0.0f;

}
