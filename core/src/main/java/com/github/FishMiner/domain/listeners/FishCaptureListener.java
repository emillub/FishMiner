package com.github.FishMiner.domain.listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.FishMiner.domain.GameContext;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.events.ecsEvents.FishCapturedEvent;

public class FishCaptureListener implements IGameEventListener<FishCapturedEvent> {

    private final GameContext gameContext;

    public FishCaptureListener(GameContext gameContext){
        this.gameContext = gameContext;
    }
    @Override
    public void onEvent(FishCapturedEvent event) {
        PooledEngine engine = gameContext.getEngine();
        if(engine != null) {
            ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
            if (players.size() > 0) {
                Entity player = players.first();
                InventoryComponent inventory = player.getComponent(InventoryComponent.class);
                if (inventory != null) {
                    inventory.money += event.getValue();
                }
            }
        }
    }

    @Override
    public Class<FishCapturedEvent> getEventType() {
        return FishCapturedEvent.class;
    }
}
