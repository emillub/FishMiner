package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;

public class StateComponent<T extends Enum<T>> implements Component {
    public T state;

    public StateComponent(T initialState) {
        this.state = initialState;
    }

    public void changeState(T newState) {
        this.state = newState;
    }

    public T getState() {
        return state;
    }
}
