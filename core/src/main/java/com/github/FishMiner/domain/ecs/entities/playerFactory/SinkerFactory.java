package com.github.FishMiner.domain.ecs.entities.playerFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.infrastructure.Logger;
import com.github.FishMiner.infrastructure.ValidateUtil;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.SinkerComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.UpgradeComponent;
import com.github.FishMiner.domain.ecs.components.WeightComponent;
import com.github.FishMiner.domain.ecs.entities.SinkerTypes;

public class SinkerFactory {
    private static final String TAG = "SinkerFactory";

    private SinkerFactory() {
    }

    protected static Entity createEntity(PooledEngine engine, String texturePath, String name, float weight, int price, float scale) {
        Entity sinker = engine.createEntity();

        SinkerComponent sinkerComponent = engine.createComponent(SinkerComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        WeightComponent weightComponent = engine.createComponent(WeightComponent.class);
        UpgradeComponent upgradeComponent = engine.createComponent(UpgradeComponent.class);

        // Upgrade logic
        if (price == 0) {
            upgradeComponent.setUpgraded(true);
            upgradeComponent.setPrice(0);
        } else {
            upgradeComponent.setType(sinker);
            upgradeComponent.setPrice(price);
        }

        // Basic visuals and config
        textureComponent.setRegion(texturePath);
        transformComponent.scale = new Vector2(scale, scale);

        // ✅ Set a safe default position to avoid spawning on the boat
        transformComponent.pos.set(-100f, -100f, 1f); // Place off-screen until attached

        sinkerComponent.name = name;
        weightComponent.weight = weight;

        sinker.add(textureComponent);
        sinker.add(upgradeComponent);
        sinker.add(attachmentComponent);
        sinker.add(transformComponent);
        sinker.add(weightComponent);
        sinker.add(sinkerComponent);

        return sinker;
    }

    protected static Entity createEntity(PooledEngine engine, SinkerTypes type) {
        validateSinkerType(type);
        String path = type.getTexturePath();
        String name = type.getName();
        float weight = type.getWeight();
        int price = type.getPrice();
        float scale = type.getScale();

        return createEntity(engine, path, name, weight, price, scale);
    }

    private static void validateSinkerType(SinkerTypes sinkerType) {
        if (sinkerType.getName().isBlank()) {
            IllegalArgumentException exception = new IllegalArgumentException("Sinker name cannot be blank");
            Logger.getInstance().error(TAG, "woops, add name to the SinkerType", exception);
            throw exception;
        }
        ValidateUtil.validateNotNull(sinkerType.getTexturePath(), "sinkerType");
        ValidateUtil.validatePositiveFloat(sinkerType.getWeight(), "sinkerType.getWeight()");
        ValidateUtil.validatePositiveFloat(sinkerType.getScale(), "sinkerType.getScale()");
    }
}
