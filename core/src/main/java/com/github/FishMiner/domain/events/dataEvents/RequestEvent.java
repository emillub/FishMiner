package com.github.FishMiner.domain.events.dataEvents;

import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ports.in.IGameEvent;
import com.github.FishMiner.ui.ports.in.IPlayer;

public class RequestEvent implements IGameEvent {
    private final static String TAG = "FetchPlayerRequest";

    private IPlayer playerCharacter;
    private boolean handled;

    public RequestEvent(IPlayer playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    @Override
    public void setHandled() {
        ValidateUtil.validateNotNull(playerCharacter, TAG + " -> playerCharacter");
        handled = true;
    }

    @Override
    public boolean isHandled() {
        return handled;
    }
}
