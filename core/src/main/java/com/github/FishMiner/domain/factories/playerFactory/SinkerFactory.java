package com.github.FishMiner.domain.factories.playerFactory;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.SinkerComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.WeightComponent;

public class SinkerFactory {
    private SinkerFactory() {
    }

    protected static Entity createEntity(PooledEngine engine, String name, int price, float weight) {
        Entity sinker = engine.createEntity();

        SinkerComponent sinkerComponent = engine.createComponent(SinkerComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        WeightComponent weightComponent = engine.createComponent(WeightComponent.class);

        sinkerComponent.name = name;
        sinkerComponent.price = price;
        textureComponent.setRegion("sinker_1cols_1rows.png");
        weightComponent.weight = 10;

        sinker.add(textureComponent);
        sinker.add(attachmentComponent);
        sinker.add(transformComponent);
        sinker.add(weightComponent);

        return sinker;
    }
}


