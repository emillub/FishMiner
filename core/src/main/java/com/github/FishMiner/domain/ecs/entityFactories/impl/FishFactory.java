//package com.github.FishMiner.domain.ecs.entityFactories.impl;
// TODO: REMOVE FILE
//import com.badlogic.ashley.core.Entity;
//import com.badlogic.gdx.graphics.g2d.Animation;
//import com.badlogic.gdx.math.Vector2;
//import com.github.FishMiner.Configuration;
//import com.github.FishMiner.domain.ecs.components.AnimationComponent;
//import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
//import com.github.FishMiner.domain.ecs.components.BoundsComponent;
//import com.github.FishMiner.domain.ecs.components.FishComponent;
//import com.github.FishMiner.domain.ecs.components.PositionComponent;
//import com.github.FishMiner.domain.ecs.components.RotationComponent;
//import com.github.FishMiner.domain.ecs.components.StateComponent;
//import com.github.FishMiner.domain.ecs.components.TextureComponent;
//import com.github.FishMiner.domain.ecs.components.VelocityComponent;
//import com.github.FishMiner.domain.ecs.entityFactories.IEntityFactory;
//import com.github.FishMiner.domain.states.EntityState;
//
//
//public class FishFactory implements IEntityFactory {
//
//
//    @Override
//    public Entity createEntity() {
//        Entity fish = new Entity();
//        Configuration config = Configuration.getInstance();
//
//        // sets fish size and value
//        fish.add(new FishComponent(32, 100));
//
//        // Position
//        int LowerSpawnPoint = config.getOceanHeight() / 2;
//        int clownFishSpawnHeight = config.getOceanHeight() - 100;
//        Vector2 initialPosition = new Vector2(config.getScreenWidth(), clownFishSpawnHeight);
//        PositionComponent fishPos = new PositionComponent(initialPosition);
//        fish.add(fishPos);
//
//        // rotation
//        fish.add(new RotationComponent(0f));
//
//        // Texture and Animation
//        TextureComponent textureComponent = new TextureComponent("clown_fish_9row_3col.png", 3, 9);
//        fish.add(textureComponent);
//
//        // Attachement to Hook
//        fish.add(new AttachmentComponent(new Vector2(10, 0)));
//
//        // Animation
//        AnimationComponent animationComponent = new AnimationComponent();
//        animationComponent.addAnimation("fishable", textureComponent,  0);
//        animationComponent.addAnimation("hooked", textureComponent, 1, Animation.PlayMode.NORMAL);
//        animationComponent.addAnimation("reeling", textureComponent, 2);
//        animationComponent.setCurrentAnimation("hooked");
//        fish.add(animationComponent);
//
//        // State component
//        fish.add(new StateComponent<>(EntityState.FishStates.FISHABLE));
//
//        // Velocity
//        fish.add(new VelocityComponent(new Vector2(-60, 0)));
//
//        // Bounds for collision detection
//        fish.add(new BoundsComponent(fishPos.position, textureComponent.getRegion().getRegionWidth(), textureComponent.getRegion().getRegionHeight()));
//
//        return fish;
//    }
//
//    private int getRandomHeightInRange(int min, int max) {
//        return (int) ((Math.random() * (max - min)) + min);
//    }
//}
