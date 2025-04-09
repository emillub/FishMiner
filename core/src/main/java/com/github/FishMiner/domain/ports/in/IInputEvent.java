package com.github.FishMiner.domain.ports.in;

import com.badlogic.ashley.core.Entity;

public interface IInputEvent {
    void setSource(Entity source);
}
