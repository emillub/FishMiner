package com.github.FishMiner.domain.listeners;

import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.events.IGameEventListener;
import com.github.FishMiner.domain.events.impl.HookReelingEvent;
import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.states.HookStates;

public class HookStateListener implements IGameEventListener<HookReelingEvent> {


    @Override
    public void onEvent(HookReelingEvent event) {
        Entity hook = event.getEventEntity();
        StateComponent stateComponent =
            hook.getComponent(StateComponent.class);
        if (stateComponent != null) {
            stateComponent.changeState(HookStates.REELING);
            System.out.println("Hook state changed to REELING via HookStateListener");
        }
        // TODO: additional logic, e.g. play sound or update UI
    }
}
