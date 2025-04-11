package com.github.FishMiner.domain.events.ecsEvents;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.ports.in.IGameEvent;

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
