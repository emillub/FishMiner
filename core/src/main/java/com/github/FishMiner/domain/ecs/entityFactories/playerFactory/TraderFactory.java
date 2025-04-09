package com.github.FishMiner.domain.ecs.entityFactories.playerFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.TraderComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.states.TraderStates;

public class TraderFactory {

    private TraderFactory() {
    }

    public static void addTraderCharacterTo(PooledEngine engine, int posX, int posY) {
        Entity trader = createTraderEntity(engine, posX, posY);
        Entity heavySinker = SinkerFactory.createEntity(engine, "Heavy Sinker", 400, 20f);

        TraderComponent traderComponent = trader.getComponent(TraderComponent.class);
        traderComponent.products.add(heavySinker);

        engine.addEntity(trader);
        engine.addEntity(heavySinker);
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


}
