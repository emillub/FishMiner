package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.events.AbstractEntityEvent;
import com.github.FishMiner.domain.ports.in.IInputEvent;


public class FireInputEvent extends AbstractEntityEvent implements IInputEvent {
    public FireInputEvent(Entity hook) {
        super(hook);
    }
}
