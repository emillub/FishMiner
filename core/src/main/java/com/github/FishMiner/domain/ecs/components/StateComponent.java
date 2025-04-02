package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;

public class StateComponent<T extends Enum<T>> implements Component {
    public T state;

    public void changeState(T newState) {
        this.state = newState;
    }

    public T getState() {
        if (state == null) {
            throw new IllegalStateException("cannot getState from entity where state is null");
        }
        return state;
    }

    public String getAnimationKey() {
        return state.toString();
    }
}
