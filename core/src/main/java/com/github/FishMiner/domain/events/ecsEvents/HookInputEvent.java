package com.github.FishMiner.domain.events.ecsEvents;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.ui.ports.in.IPlayer;


public class HookInputEvent extends AbstractEntityEvent  {
    public HookInputEvent(IPlayer player) {
        super(player.getHook());
    }

    @Override
    public void setHandled() {
        super.setHandled();
    }

}
