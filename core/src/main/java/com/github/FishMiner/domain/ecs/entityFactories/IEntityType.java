package com.github.FishMiner.domain.ecs.entityFactories;

public interface IEntityType {
    // Common properties for every entity type i.e Fish, Garbage, Shark.
    String getTexturePath();
    int getFrameCols();
    int getFrameRows();
    int[] getAllowedDepthLevels();
    int getWeight();

    /**
     * Returns the speed of the entity.
     * Default implementation returns 0, which may be appropriate for entities that don't move.
     * Entities such as FishTypes can override this method.
     */
    default int getSpeed() {
        return 0;
    }

    /**
     * Returns the scale of the entity.
     * Default implementation returns 1.0f.
     * Entities that need a different scale (such as FishTypes) can override this method.
     */
    default float getScale() {
        return 1.0f;
    }
}
