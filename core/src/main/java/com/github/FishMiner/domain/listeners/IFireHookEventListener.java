package com.github.FishMiner.domain.listeners;

import com.github.FishMiner.domain.events.impl.FireInputEvent;

public interface IFireHookEventListener {
    void onEvent(FireInputEvent event);
}
