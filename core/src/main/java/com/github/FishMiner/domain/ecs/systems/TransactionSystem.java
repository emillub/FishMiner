package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.github.FishMiner.domain.events.impl.TransactionEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;


public class TransactionSystem extends EntitySystem implements IGameEventListener<TransactionEvent> {

    public TransactionSystem() {

    }

    @Override
    public void onEvent(TransactionEvent event) {

    }

    @Override
    public Class<TransactionEvent> getEventType() {
        return TransactionEvent.class;
    }
}
