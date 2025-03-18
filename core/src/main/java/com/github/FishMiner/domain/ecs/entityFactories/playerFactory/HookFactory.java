package com.github.FishMiner.domain.ecs.entityFactories.playerFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.states.HookStates;
//import com.github.FishMiner.domain.states.HookStates;

public class HookFactory {

        public static Entity createEntity(int x, int y) {
            Entity hook = new Entity();

            hook.add(new HookComponent());

            // Position
            Vector2 initialPosition = new Vector2(x, y);
            PositionComponent position = new PositionComponent(initialPosition);
            hook.add(position);

            // rotation
            hook.add(new RotationComponent(0f));

            // Texture and Animation
            TextureComponent textureComponent = new TextureComponent("hook_1cols_1rows.png", 1, 1);
            hook.add(textureComponent);

            // Velocity
            hook.add(new VelocityComponent(new Vector2(0, 0)));

            // Bounds for collision detection
            BoundsComponent bounds = new BoundsComponent();
            bounds.bounds.setPosition(position.position);
            bounds.bounds.setWidth(textureComponent.getFrameWidth());
            bounds.bounds.setHeight(textureComponent.getFrameHeight());
            hook.add(bounds);

            // Add a StateComponent with a default state (SWINGING)
            hook.add(new StateComponent<HookStates>(HookStates.SWINGING));

            return hook;
        }
}
