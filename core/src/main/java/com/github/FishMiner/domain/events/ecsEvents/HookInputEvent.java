package com.github.FishMiner.domain.events.ecsEvents;

import com.github.FishMiner.ui.ports.in.IPlayer;


public class HookInputEvent extends AbstractEntityEvent {
    public HookInputEvent(IPlayer player) {
        super(player.getHook());
    }
}
