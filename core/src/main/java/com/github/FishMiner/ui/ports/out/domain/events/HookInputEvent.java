package com.github.FishMiner.ui.ports.out.domain.events;

import com.github.FishMiner.domain.ecs.events.AbstractEntityEvent;
import com.github.FishMiner.ui.ports.in.domain.interfaces.IPlayer;


public class HookInputEvent extends AbstractEntityEvent {
    public HookInputEvent(IPlayer player) {
        super(player.getHook());
    }
}
