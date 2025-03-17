package com.github.FishMiner.domain.listeners;

import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.events.impl.FishHitEvent;
import com.github.FishMiner.domain.states.FishableObjectStates;

public class FishStateListener implements IGameEventListener<FishHitEvent> {

    @Override
    @SuppressWarnings("unchecked")
    public void onEvent(FishHitEvent event) {
        StateComponent stateComponent = event.getEventEntity().getComponent(StateComponent.class);
        if (stateComponent != null) {
            stateComponent.changeState(FishableObjectStates.HOOKED);
        }
        //fish.getComponent(StateComponent.class).changeState(EntityState.FishStates.HOOKED);
        // TODO: trigger additional logic, such as playing a sound or updating the score.
    }

    @Override
    public Class<FishHitEvent> getEventType() {
        return FishHitEvent.class;
    }
}
