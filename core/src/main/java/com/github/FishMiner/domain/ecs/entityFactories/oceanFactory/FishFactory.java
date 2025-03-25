package com.github.FishMiner.domain.ecs.entityFactories.oceanFactory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.util.FishUtils;
import com.github.FishMiner.domain.states.FishableObjectStates;

public class FishFactory {

    public static final int EURO_FACTOR = 10;
    private final Engine engine;

    protected FishFactory(Engine engine) {
        this.engine = engine;
    }

    protected Entity createEntity(FishTypes type) {
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

    @SuppressWarnings("unchecked")
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

        textureComponent.setRegion(texturePath, frameCols, frameRows);
        System.out.println(textureComponent.texturePath);

        fishComponent.setDepthLevel(depthLevel);
        fishComponent.setWeight(weight);
        fishComponent.setBaseSpeed(speed);
        fishComponent.width = textureComponent.getFrameWidth();
        fishComponent.height = textureComponent.getFrameHeight();

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

        attachmentComponent.offset.x = 10;

        stateComponent.changeState(FishableObjectStates.FISHABLE);

        animationComponent.addAnimation(FishableObjectStates.FISHABLE.getAnimationKey(), textureComponent, 0);
        animationComponent.addAnimation(FishableObjectStates.HOOKED.getAnimationKey(), textureComponent, 1, Animation.PlayMode.NORMAL);
        animationComponent.setCurrentAnimation(stateComponent.getState().getAnimationKey());
        animationComponent.addAnimation(FishableObjectStates.REELING.getAnimationKey(), textureComponent, 1);
        animationComponent.setCurrentAnimation(FishableObjectStates.FISHABLE.getAnimationKey());

        fish.add(transformComponent);
        fish.add(textureComponent);
        fish.add(velocityComponent);
        fish.add(boundsComponent);
        fish.add(attachmentComponent);
        fish.add(stateComponent);
        fish.add(animationComponent);

        return fish;
    }

    public static int calculateFishValue(int depth, int speed, int weight) {
        float value = (float) (depth * speed * weight) / 100;
        return Math.round(value * EURO_FACTOR);
    }
}
