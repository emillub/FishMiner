package com.github.FishMiner.domain.ecs.entityFactories.impl;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.util.FishUtils;
import com.github.FishMiner.domain.states.FishableObjectStates;

public class FishFactory {

    private final Engine engine;
    public FishFactory(Engine engine) {
        this.engine = engine;
    }

    public Entity createEntity(FishTypes type) {
        int[] allowedDepths = type.getAllowedDepthLevels();
        int chosenDepthLevel = allowedDepths[MathUtils.random(allowedDepths.length - 1)];

        System.out.println("Spawning " + type.name() + " in depth level " + chosenDepthLevel);

        return createEntity(
            type.getTexturePath(),
            type.getFrameCols(),
            type.getFrameRows(),
            chosenDepthLevel,
            type.getSpeed(),
            type.getWeight(),
            type
        );
    }

    protected Entity createEntity(String texturePath, int frameCols, int frameRows, int depthLevel, float speed, int weight, FishTypes type) {
        Entity fish = new Entity();
        FishComponent fishComponent = engine.createComponent(FishComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        VelocityComponent velocityComponent = engine.createComponent(VelocityComponent.class);
        BoundsComponent boundsComponent = engine.createComponent(BoundsComponent.class);
        TextureComponent textureComponent = new TextureComponent();
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);
        StateComponent<FishableObjectStates> stateComponent = engine.createComponent(StateComponent.class);
        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);

        //int fishValue = (int) ((float) depthLevel * weight * Math.abs(speed));
        fishComponent.setDepthLevel(depthLevel);
        fishComponent.setWeight(weight);
        fishComponent.setBaseSpeed(speed);
        fishComponent.width = 62;
        fishComponent.height = 30;

        // spawns to the left or the right
        boolean movesRight = MathUtils.randomBoolean();

        transformComponent.pos = new Vector3(
            FishUtils.getFishStartPosX(movesRight, textureComponent.getFrameWidth()),
            FishUtils.getRandomDepthFor(depthLevel, fishComponent.height),
            0
        );

        velocityComponent.velocity.x  = FishUtils.getFishDirectionX(movesRight, speed);

        boundsComponent.bounds.setX(transformComponent.pos.x);
        boundsComponent.bounds.setY(transformComponent.pos.y);
        boundsComponent.bounds.setWidth(textureComponent.getFrameWidth());
        boundsComponent.bounds.setHeight(textureComponent.getFrameHeight());

        textureComponent.setRegion(texturePath, frameCols, frameRows);

        attachmentComponent.offset.x = 10;

        stateComponent.changeState(FishableObjectStates.FISHABLE);

        animationComponent.addAnimation(FishableObjectStates.FISHABLE.getAnimationKey(), textureComponent, 0);
        animationComponent.addAnimation(FishableObjectStates.HOOKED.getAnimationKey(), textureComponent, 1, Animation.PlayMode.NORMAL);
//      animationComponent.addAnimation(FishableObjectStates.REELING.getAnimationKey(), textureComponent, 2);
        animationComponent.setCurrentAnimation(stateComponent.getState().getAnimationKey());

        fish.add(transformComponent);
        fish.add(textureComponent);
        fish.add(velocityComponent);
        fish.add(boundsComponent);
        fish.add(attachmentComponent);
        fish.add(stateComponent);
        fish.add(animationComponent);

        return fish;
    }
}
