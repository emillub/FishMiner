package com.github.FishMiner.domain.ecs.entityFactories.oceanFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;

import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.SharkComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.components.WeightComponent;
import com.github.FishMiner.domain.ecs.entityFactories.SharkTypes;
import com.github.FishMiner.domain.ecs.util.FishUtils;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;
import com.github.FishMiner.domain.states.FishableObjectStates;

public class SharkFactory {
        private static final String TAG = "SharkFactory";
        private final PooledEngine engine;
        protected SharkFactory(PooledEngine engine) {
            this.engine = engine;
        }

        protected Entity createEntity(SharkTypes type) {
            try {
                int[] allowedDepths = type.getAllowedDepthLevels();
                int chosenDepthLevel = allowedDepths[MathUtils.random(allowedDepths.length - 1)];

                validateSharkFields(
                    type.getTexturePath(),
                    type.getFrameCols(),
                    type.getFrameRows(),
                    chosenDepthLevel,
                    type.getSpeed(),
                    type.getWeight(),
                    type.getScale()
                );
                return createEntity(
                    type.getTexturePath(),
                    type.getFrameCols(),
                    type.getFrameRows(),
                    chosenDepthLevel,
                    type.getSpeed(),
                    type.getWeight(),
                    type.getScale()
                );
            } catch (IllegalArgumentException e) {
                Logger.getInstance().error(TAG, "Invalid value(s) for createEntity: " + e.getMessage(), e);
                throw e;
            }
        }

        @SuppressWarnings("unchecked")
        protected Entity createEntity(String texturePath, int frameCols, int frameRows, int depthLevel, float speed, int weight, float scale) {
            Entity fish = engine.createEntity();

            SharkComponent sharkComponent = engine.createComponent(SharkComponent.class);
            FishComponent fishComponent = engine.createComponent(FishComponent.class);
            TransformComponent transform = engine.createComponent(TransformComponent.class);
            VelocityComponent velocity = engine.createComponent(VelocityComponent.class);
            BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
            TextureComponent texture = engine.createComponent(TextureComponent.class);
            AttachmentComponent attachment = engine.createComponent(AttachmentComponent.class);
            StateComponent<FishableObjectStates> state = engine.createComponent(StateComponent.class);
            AnimationComponent animation = engine.createComponent(AnimationComponent.class);
            WeightComponent weightComp = engine.createComponent(WeightComponent.class);

            texture.setRegion(texturePath, frameCols, frameRows);
            sharkComponent.setDamage(-1.5f);
            fishComponent.setDepthLevel(depthLevel);
            fishComponent.setWeight(weight);
            fishComponent.setBaseSpeed(speed);
            fishComponent.width = texture.getFrameWidth();
            fishComponent.height = texture.getFrameHeight();

            weightComp.weight = fishComponent.weight;

            boolean movesRight = MathUtils.randomBoolean();
            float screenWidth = Configuration.getInstance().getScreenWidth();

            float startX = movesRight ? -fishComponent.width : screenWidth + fishComponent.width;

            // Start Y: based on depth
            float startY = FishUtils.getRandomDepthFor(depthLevel, fishComponent.height);
            transform.pos = new Vector3(startX, startY, 0);
            transform.scale.set(scale, scale);
            velocity.velocity.x = FishUtils.getFishDirectionX(movesRight, speed);

            bounds.bounds.set(
                transform.pos.x,
                transform.pos.y,
                fishComponent.width,
                fishComponent.height
            );

            // TODO: is the attachment offset used?
            attachment.offset.x = 10;

            state.changeState(FishableObjectStates.FISHABLE);

            animation.addAnimation(FishableObjectStates.FISHABLE.getAnimationKey(), texture, 0);
            //Todo: set this to stateRow 1 when all Fish has row
            animation.addAnimation(FishableObjectStates.ATTACKING.getAnimationKey(), texture, 0, Animation.PlayMode.NORMAL);
            animation.setCurrentAnimation(FishableObjectStates.FISHABLE.getAnimationKey());

            fish.add(transform);
            fish.add(texture);
            fish.add(velocity);
            fish.add(bounds);
            fish.add(attachment);
            fish.add(state);
            fish.add(animation);
            fish.add(weightComp);
            fish.add(fishComponent);

            return fish;
        }

        private void validateSharkFields(String texturePath, int frameCols, int frameRows, int depthLevel, float speed, int weight, float scale) {
            ValidateUtil.validateNotNull(texturePath, "texturePath");
            ValidateUtil.validatePositiveInt(depthLevel, "depthLevel");
            ValidateUtil.validatePositiveInt(frameCols, "frameCols");
            ValidateUtil.validatePositiveInt(frameRows, "frameRows");
            ValidateUtil.validatePositiveInt(weight, "weight");
            ValidateUtil.validatePositiveFloat(speed, "speed");
            ValidateUtil.validatePositiveFloat(scale, "scale");
        }
}
