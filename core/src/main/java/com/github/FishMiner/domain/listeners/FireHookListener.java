package com.github.FishMiner.domain.listeners;


import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.events.IEventListener;
import com.github.FishMiner.domain.events.impl.FishHitEvent;
import com.github.FishMiner.domain.states.EntityState;

public class FireHookListener implements IEventListener<FishHitEvent> {

    //TODO: update this
    @Override
    @SuppressWarnings("unchecked")
    public void onEvent(FishHitEvent event) {
        StateComponent<EntityState.HookStates> stateComponent = event.getEventEntity().getComponent(StateComponent.class);
        if (stateComponent != null) {
            stateComponent.changeState(EntityState.HookStates.FIRE);
        }
        // TODO: trigger additional logic, such as playing a sound or updating the score.
    }
}
