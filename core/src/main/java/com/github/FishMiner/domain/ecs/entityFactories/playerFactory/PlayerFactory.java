package com.github.FishMiner.domain.ecs.entityFactories.playerFactory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.states.HookStates;

public class PlayerFactory {


    private PlayerFactory() {
        // prevent init
    }

    public  void addPlayerCharacter(Engine engine, int posX, int posY) {
        Configuration config = Configuration.getInstance();
        Entity player = createPlayer(
            config.getScreenWidth() / 2,
            (int) (config.getScreenHeight() * config.getOceanHeightPercentage())
        );

        Entity hook = createHook(player);

        engine.addEntity(player);
        engine.addEntity(hook);
    }

    private static Entity createPlayer(int posX, int posY) {
        Entity player = new Entity();
        player.add(new TextureComponent("fisheman.png", 1, 1));
        player.add(new PositionComponent(new Vector2(posX, posY)));

        return player;
    }

    private Entity createHook(Entity player) {
        Entity hook = new Entity();

        hook.add(new HookComponent());

        // Position
        Vector2 initialPosition = new Vector2(0, 0);
        PositionComponent positionComponent = new PositionComponent(initialPosition);
        hook.add(positionComponent);

        // rotation
        hook.add(new RotationComponent(0f));

        // Texture and Animation
        TextureComponent textureComponent = new TextureComponent("hook_1cols_1rows.png", 1, 1);
        hook.add(textureComponent);

        // Velocity
        hook.add(new VelocityComponent(new Vector2(0, 0)));

        // Bounds for collision detection
        hook.add(new BoundsComponent(initialPosition, textureComponent.getRegion().getRegionWidth(), textureComponent.getRegion().getRegionHeight()));

        // Add a StateComponent with a default state (SWINGING)
        hook.add(new StateComponent<>(HookStates.SWINGING));

        hook.add(new AttachmentComponent(new Vector2(100, -50), player));
        return hook;
    }
}

