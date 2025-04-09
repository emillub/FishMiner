package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.events.AbstractEntityEvent;
import com.github.FishMiner.domain.ports.in.IGameEvent;

public class TransactionEvent extends AbstractEntityEvent implements IGameEvent {
    public TransactionEvent(Entity buyer, Entity product) {
        super(product);
        super.setSource(buyer);
    }
}
