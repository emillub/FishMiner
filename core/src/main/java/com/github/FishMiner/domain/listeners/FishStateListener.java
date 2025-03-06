package com.github.FishMiner.domain.listeners;

import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.events.IEventListener;
import com.github.FishMiner.domain.events.impl.FishHitEvent;
import com.github.FishMiner.domain.states.EntityState;

public class FishStateListener implements IEventListener<FishHitEvent> {

    @Override
    @SuppressWarnings("unchecked")
    public void onEvent(FishHitEvent event) {
        StateComponent<EntityState.FishStates> stateComponent = event.getEventEntity().getComponent(StateComponent.class);
        if (stateComponent != null) {
            stateComponent.changeState(EntityState.FishStates.HOOKED);
        }
        //fish.getComponent(StateComponent.class).changeState(EntityState.FishStates.HOOKED);
        // TODO: trigger additional logic, such as playing a sound or updating the score.
    }
}
