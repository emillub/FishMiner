package com.github.FishMiner.domain.listeners;

import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.events.IEventListener;
import com.github.FishMiner.domain.events.impl.HookReelingEvent;
import com.github.FishMiner.domain.states.EntityState;
import com.badlogic.ashley.core.Entity;

public class HookStateListener implements IEventListener<HookReelingEvent> {

    @Override
    @SuppressWarnings("unchecked")
    public void onEvent(HookReelingEvent event) {
        Entity hook = event.getEventEntity();
        StateComponent<EntityState.HookStates> stateComponent =
            hook.getComponent(StateComponent.class);
        if (stateComponent != null) {
            stateComponent.changeState(EntityState.HookStates.REELING);
            System.out.println("Hook state changed to REELING via HookStateListener");
        }
        // TODO: additional logic, e.g. play sound or update UI
    }
}
