package com.github.FishMiner.domain.ecs.entityFactories;

// Declares methods for creating each type of entity

import com.badlogic.ashley.core.Entity;

import java.util.LinkedList;
import java.util.List;


/**
 * Classes that implement this interface can create lists of various Fish, Trash, and all the components that make up a player
 */
public interface IGameEntityFactory {
    LinkedList<Entity> createFish(FishTypes type, int amount);
}


