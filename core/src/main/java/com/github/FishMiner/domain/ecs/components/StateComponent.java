package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.github.FishMiner.domain.states.IState;


public class StateComponent<T extends IState> implements Component {
    public T state;

    public StateComponent(T initialState) {
        this.state = initialState;
    }

    public void changeState(T newState) {
        if (state != null) {
            state.onExit();
        }
        state = newState;
        state.onEnter();
    }
}
