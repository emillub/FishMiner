package com.github.FishMiner.domain.ecs.entityFactories.playerFactory;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.WeightComponent;
import com.github.FishMiner.domain.ecs.entityFactories.IEntityFactory;

public class SinkerFactory implements IEntityFactory {

    private final Engine engine;

    public SinkerFactory(Engine engine) {
        this.engine = engine;
    }

    @Override
    public Entity createEntity(int posX, int posY) {
        Entity sinker = new Entity();
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        WeightComponent weightComponent = engine.createComponent(WeightComponent.class);

        textureComponent.setRegion("sinker_1cols_1rows.png");
        transformComponent.pos = new Vector3(100, 100, 1);
        weightComponent.weight = 10;

        sinker.add(textureComponent);
        sinker.add(attachmentComponent);
        sinker.add(transformComponent);
        sinker.add(weightComponent);

        return sinker;
    }
}


