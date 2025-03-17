package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import java.util.LinkedList;

/**
 * Component that holds a queue of fish entities waiting to be spawned.
 */
public class SpawnQueueComponent implements Component {
    public final LinkedList<Entity> fishQueue = new LinkedList<>();
    public float spawnInterval = 1f;

    /**
     * Adds a fish entity to the spawn queue.
     *
     * @param fish The fish entity to be added.
     */
    public void addFish(Entity fish) {
        fishQueue.add(fish);
    }

    /**
     * Retrieves and removes the next fish entity from the queue.
     *
     * @return The next fish entity, or null if the queue is empty.
     */
    public Entity pollFish() {
        return fishQueue.poll();
    }

    /**
     * Checks if the queue is empty.
     *
     * @return True if empty, false otherwise.
     */
    public boolean isEmpty() {
        return fishQueue.isEmpty();
    }

    /**
     * Updates the spawn interval dynamically.
     *
     * @param interval The new spawn interval in seconds.
     */
    public void setSpawnInterval(float interval) {
        this.spawnInterval = interval;
    }
}

