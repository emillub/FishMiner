package com.github.FishMiner.domain.listeners;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.FishMiner.domain.World;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.events.impl.FishCapturedEvent;

public class FishCaptureListener implements IGameEventListener<FishCapturedEvent> {

    private final World world;

    public FishCaptureListener(World world){
        this.world = world;
    }
    @Override
    public void onEvent(FishCapturedEvent event) {
        Engine engine = world.getEngine();
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
