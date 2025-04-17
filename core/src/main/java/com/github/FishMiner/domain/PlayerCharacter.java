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

    public PlayerCharacter(PooledEngine engine, int posX, int posY) {
        this.playerEntity = PlayerFactory.addNewPlayerCharacterTo(engine, posX, posY);
        ValidateUtil.validateNotNull(playerEntity, "PlayerCharacter -> playerEntity");
    }

    public Entity getPlayerEntity() {
        return playerEntity;
    }

    public Entity getHook() {
        return playerEntity.getComponent(PlayerComponent.class).getHook();
    }

    public Entity getSinker() {
        return playerEntity.getComponent(PlayerComponent.class).getSinker();
    }

    public Entity getReel() {
        return playerEntity.getComponent(PlayerComponent.class).getReel();
    }

    @Override
    public int getScore() {
        return (int) playerEntity.getComponent(ScoreComponent.class).getScore();
    }
}
