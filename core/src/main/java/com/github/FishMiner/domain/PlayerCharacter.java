package com.github.FishMiner.domain;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
import com.github.FishMiner.domain.ecs.components.ScoreComponent;
import com.github.FishMiner.domain.factories.playerFactory.PlayerFactory;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.ui.ports.in.IPlayer;

public class PlayerCharacter implements IPlayer {
    private final Entity playerEntity;
    private static PlayerCharacter instance;
    private static boolean isCreated = false;
    public PlayerCharacter(PooledEngine engine, int posX, int posY) {
        PlayerFactory.addNewPlayerCharacterTo(engine, posX, posY);
        playerEntity = PlayerFactory.getPlayer();
        ValidateUtil.validateNotNull(playerEntity, "PlayerCharacter -> playerEntity");
    }

    public static PlayerCharacter getInstance(PooledEngine engine,int posX, int posY) {
        if (instance == null && !isCreated) {
            instance = new PlayerCharacter(engine, posX, posY);
            isCreated = true;
        }
        return instance;
    }

    public Entity getPlayerEntity() {
        return playerEntity;
    }

    public Entity getHook() {
        return playerEntity.getComponent(PlayerComponent.class).hook;
    }
    public Entity getSinker() {
        return playerEntity.getComponent(PlayerComponent.class).sinker;
    }

    public Entity getReel() {
        return playerEntity.getComponent(PlayerComponent.class).reel;
    }

    @Override
    public int getScore() {
        return (int) playerEntity.getComponent(ScoreComponent.class).getScore();
    }


}
