package com.github.FishMiner.ui.ports.out.domain.events;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.ecs.events.AbstractEntityEvent;
import com.github.FishMiner.infrastructure.ports.in.IGameEvent;

public class TransactionEvent extends AbstractEntityEvent implements IGameEvent {
    private boolean isApproved;
    public TransactionEvent(Entity buyer, Entity product) {
        super(product);
        super.setSource(buyer);
    }
    public void setApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public boolean getApproved() {
        return isApproved;
    }
}
