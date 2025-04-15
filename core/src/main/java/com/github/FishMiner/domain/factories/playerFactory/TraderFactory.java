package com.github.FishMiner.domain.factories.playerFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.TraderComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.factories.HookTypes;
import com.github.FishMiner.domain.factories.ReelTypes;
import com.github.FishMiner.domain.factories.SinkerTypes;
import com.github.FishMiner.domain.states.TraderStates;

public class TraderFactory {
    private static Entity traderEntity;
    private TraderFactory() {
    }

    public static void addNewTraderTo(PooledEngine engine, int posX, int posY) {
        traderEntity = createTraderEntity(engine, posX, posY);
        Entity heavySinker = SinkerFactory.createEntity(engine, SinkerTypes.HEAVY_SINKER);
        Entity sharpHook = HookFactory.createEntity(engine, HookTypes.SHARP_HOOK, posY, new Vector3(posX, posY, 0), traderEntity); //dummy values
        Entity longReel = ReelFactory.createEntity(engine, ReelTypes.Long_Reel);
        Entity heavierSinker = SinkerFactory.createEntity(engine, SinkerTypes.HEAVIER_SINKER);

        TraderComponent traderComponent = traderEntity.getComponent(TraderComponent.class);
        traderComponent.addProduct(heavySinker);
        traderComponent.addProduct(sharpHook);
        traderComponent.addProduct(longReel);
        traderComponent.addProduct(heavierSinker);

        engine.addEntity(traderEntity);
        engine.addEntity(heavySinker);
        //engine.addEntity(sharpHook);
    }

    @SuppressWarnings("unchecked")
    private static Entity createTraderEntity(PooledEngine engine, int posX, int posY) {
        Entity trader = engine.createEntity();

        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        TraderComponent traderComponent = engine.createComponent(TraderComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        StateComponent<TraderStates> stateComponent = engine.createComponent(StateComponent.class);
        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);

        textureComponent.setRegion("trader_9cols_2rows.png", 9, 2);

        animationComponent.addAnimation(TraderStates.NORMAL.getAnimationKey(), textureComponent, 0, Animation.PlayMode.LOOP_RANDOM);
        animationComponent.addAnimation(TraderStates.BUY.getAnimationKey(), textureComponent, 1, Animation.PlayMode.LOOP_RANDOM);

        transformComponent.pos.set(
            posX,
            posY,
            2
        );

        stateComponent.changeState(TraderStates.NORMAL);

        trader.add(textureComponent);
        trader.add(animationComponent);
        trader.add(stateComponent);
        trader.add(transformComponent);
        trader.add(traderComponent);

        return trader;
    }

    public static Entity getTraderEntity() {
        ValidateUtil.validateNotNull(traderEntity, "trader");
        return traderEntity;
    }




}
