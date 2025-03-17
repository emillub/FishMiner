package com.github.FishMiner.domain.listeners;


import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.events.IGameEventListener;
import com.github.FishMiner.domain.events.impl.FishHitEvent;
import com.github.FishMiner.domain.states.HookStates;

public class FireHookListener implements IGameEventListener<FishHitEvent> {

    //TODO: update this
    @Override
    @SuppressWarnings("unchecked")
    public void onEvent(FishHitEvent event) {
        StateComponent stateComponent = event.getEventEntity().getComponent(StateComponent.class);
        if (stateComponent != null) {
            stateComponent.changeState(HookStates.FIRE);
        }
        // TODO: trigger additional logic, such as playing a sound or updating the score.
    }
}
