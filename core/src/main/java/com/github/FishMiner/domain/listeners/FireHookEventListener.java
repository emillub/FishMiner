package com.github.FishMiner.domain.listeners;


import com.github.FishMiner.domain.events.impl.FireInputEvent;

public class FireHookEventListener implements IGameEventListener<FireInputEvent> {

    IFireHookEventListener listener;
    @Override
    @SuppressWarnings("unchecked")
    public void onEvent(FireInputEvent event) {
        listener.onEvent(event);
    }

    public void setListener(IFireHookEventListener listener) {
        this.listener = listener;
    }

    @Override
    public Class<FireInputEvent> getEventType() {
        return FireInputEvent.class;
    }
}
