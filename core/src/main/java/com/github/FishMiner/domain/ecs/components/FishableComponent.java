package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.github.FishMiner.domain.states.EntityState;


public class FishableComponent implements Component {
    public EntityState.FishStates state = EntityState.FishStates.FISHABLE; // Default state

    public void changeState(EntityState.FishStates newState) {
        if (state != null) {
            state.onExit();
        }
        state = newState;
        state.onEnter();
    }
}
