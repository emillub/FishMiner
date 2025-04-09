package com.github.FishMiner.domain.ports.in;

/**
 * An inbound port for dispatching and managing game events within the domain.
 */
public interface IGameEventBus {

    /**
     * Registers an event listener for a specific type of game event.
     *
     * @param listener the listener instance that will handle events of its declared type.
     * @param <E>      the type of the game event.
     */
    <E extends IGameEvent> void register(IGameEventListener<E> listener);

    /**
     * Unregisters an event listener for a specific type of game event.
     *
     * @param listener the listener to remove.
     * @param <E>      the type of the game event.
     */
    <E extends IGameEvent> void unregister(IGameEventListener<E> listener);

    /**
     * Posts a game event to the event bus so that all registered listeners for that event type
     * can be notified.
     *
     * @param event the game event to post.
     */
    void post(IGameEvent event);
}
