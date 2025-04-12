package com.github.FishMiner.domain;

import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ports.in.IGameEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEventBus {
    private final static String TAG = "GameEventBus";
    private static GameEventBus instance;
    private Map<Class<? extends IGameEvent>, List<IGameEventListener<? extends IGameEvent>>> listeners;

    private GameEventBus() {
        listeners = new HashMap<>();
    }

    public static GameEventBus getInstance() {
        if (instance == null) {
            instance = new GameEventBus();
        }
        return instance;
    }

    public <E extends IGameEvent> void register(IGameEventListener<E> listener) {
        Class<E> eventType = listener.getEventType();
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
        Logger.getInstance().log(TAG, "Listener registered: " + listener.getClass().getName() + ". Type: " + listener.getEventType());
    }

    public <E extends IGameEvent> void unregister(IGameEventListener<E> listener) {
        Class<E> eventType = listener.getEventType();
        List<IGameEventListener<? extends IGameEvent>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
            Logger.getInstance().log(TAG, "Listener unregistered: " + listener.getClass().getName());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void post(IGameEvent event) {
        List<IGameEventListener<? extends IGameEvent>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (IGameEventListener listener : eventListeners) {
                listener.onEvent(event);
                Logger.getInstance().debug(TAG, "Dispatching event: " + event.getClass().getName() + " to listener: " + listener.getClass().getName());
            }
        } else {
            Logger.getInstance().debug(TAG, "No listeners registered for event: " + event.getClass().getName());
        }
    }
}
