//package com.github.FishMiner.domain.ecs.entityFactories.impl;
// TODO: REMOVE FILE
//import com.badlogic.ashley.core.Entity;
//import com.badlogic.gdx.math.Vector2;
//import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
//import com.github.FishMiner.domain.ecs.components.RotationComponent;
//import com.github.FishMiner.domain.ecs.components.TextureComponent;
//import com.github.FishMiner.domain.ecs.components.WeightComponent;
//import com.github.FishMiner.domain.ecs.entityFactories.IEntityFactory;
//
//
//public class SinkerFactory implements IEntityFactory {
//        @Override
//        public Entity createEntity() {
//            Entity sinker = new Entity();
//
//
//            sinker.add(new AttachmentComponent(new Vector2(0, 0)));
//
//            // rotation
//            sinker.add(new RotationComponent(0f));
//
//            // Texture and Animation
//            TextureComponent textureComponent = new TextureComponent("hook.png");
//            sinker.add(textureComponent);
//
//            sinker.add(new WeightComponent(20));
//
//            return sinker;
//        }
//    }
//
//
