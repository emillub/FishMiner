//package com.github.FishMiner.domain.ecs.entityFactories.impl;
// TODO: REMOVE FILE
//import com.badlogic.ashley.core.Entity;
//import com.badlogic.gdx.math.Vector2;
//import com.github.FishMiner.Configuration;
//import com.github.FishMiner.domain.ecs.components.BoundsComponent;
//import com.github.FishMiner.domain.ecs.components.HookComponent;
//import com.github.FishMiner.domain.ecs.components.PositionComponent;
//import com.github.FishMiner.domain.ecs.components.RotationComponent;
//import com.github.FishMiner.domain.ecs.components.StateComponent;
//import com.github.FishMiner.domain.ecs.components.TextureComponent;
//import com.github.FishMiner.domain.ecs.components.VelocityComponent;
//import com.github.FishMiner.domain.ecs.entityFactories.IEntityFactory;
//import com.github.FishMiner.domain.states.EntityState;
//
//
//public class HookFactory implements IEntityFactory {
//
//        @Override
//        public Entity createEntity() {
//            Entity hook = new Entity();
//            Configuration config = Configuration.getInstance();
//
//            hook.add(new HookComponent(0.5f, 0.5f));
//
//            // Position
//            Vector2 initialPosition = new Vector2((float) config.getScreenWidth() / 2, config.getOceanHeight() - 100);
//            PositionComponent positionComponent = new PositionComponent(initialPosition);
//            hook.add(positionComponent);
//
//            // rotation
//            hook.add(new RotationComponent(0f));
//
//            // Texture and Animation
//            TextureComponent textureComponent = new TextureComponent("hook.png");
//            hook.add(textureComponent);
//
//            // Velocity
//            hook.add(new VelocityComponent(new Vector2(0, 0)));
//
//            // Bounds for collision detection
//            hook.add(new BoundsComponent(initialPosition, textureComponent.getRegion().getRegionWidth(), textureComponent.getRegion().getRegionHeight()));
//
//            // Add a StateComponent with a default state (SWINGING)
//            hook.add(new StateComponent<>(EntityState.HookStates.SWINGING));
//
//            return hook;
//        }
//}
//
