package com.github.FishMiner.domain.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
    private static EventBus instance;
    private Map<Class<? extends IGameEvent>, List<IEventListener<? extends IGameEvent>>> listeners;

    private EventBus() {
        listeners = new HashMap<>();
    }

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public <E extends IGameEvent> void register(Class<E> eventType, IEventListener<E> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public <E extends IGameEvent> void unregister(Class<E> eventType, IEventListener<E> listener) {
        List<IEventListener<? extends IGameEvent>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * Safe unchecked cast since we register listeners with the correct type
     * @param event any event class that implements the GameEvent interface
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void post(IGameEvent event) {
        List<IEventListener<? extends IGameEvent>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (IEventListener listener : eventListeners) {
                listener.onEvent(event);
            }
        }
    }
}
