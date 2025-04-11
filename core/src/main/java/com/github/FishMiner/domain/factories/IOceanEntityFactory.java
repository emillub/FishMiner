package com.github.FishMiner.domain.factories;

// Declares methods for creating each type of entity

import com.badlogic.ashley.core.Entity;

import java.util.LinkedList;


/**
 * Classes that implement this interface can create lists of various Fish, Trash, and all the components that make up a player
 */
public interface IOceanEntityFactory {
    Entity createEntity(IEntityType type);
    LinkedList<Entity> createFish(FishTypes type, int amount);
    LinkedList<Entity> createGarbage(GarbageTypes type, int amount);
    LinkedList<Entity> createSharks(SharkTypes type, int amount);

}


