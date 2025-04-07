package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;

public class FishComponent implements Component {

    /**
     * The depth level is a positive value determined by the number of "lanes" in the ocean,
     * by using a value defined in the World (currently in Configuration)
     * The depthLevel is translated into the Fish Entity's vertical position in the ocean.
     */
    public int depthLevel;

    /**
     * The weight determines the weight assigned to the Fish Entity's WeightComponent.
     * The weight also contributes to the value of the fish.
     * @see WeightComponent
     */
    public int weight;
    /**
     * the horizontal speed of a fish. Common for all fish of the same FishTypes
     * @see com.github.FishMiner.domain.ecs.entityFactories.FishTypes
     *
     * The baseSpeed along with the scaled Velocity determines the speed in the Fish Entity's Velocity Component.
     * @see VelocityComponent
     */
    public float baseSpeed;

    /**
     * Common default width value. Can be overwritten.
     * Determines the width of the texture and the width of the bounds.
     * @see BoundsComponent
     */
    public int width = 62;

    /**
     * Common default height value. Can be overwritten.
     * Determines the height of the texture and the height of the bounds.
     * @see BoundsComponent
     */
    public int height = 30;

    public int getValue() {
        ValidateUtil.validatePositiveNumbers(depthLevel, weight, (int) baseSpeed);
        return (int) Math.abs(depthLevel * weight * baseSpeed);
    }

    public void setDepthLevel(int depthLevel) {
        int totalDepthLevels =  Configuration.getInstance().getDepthLevels();
        if (depthLevel < 0 || depthLevel > totalDepthLevels) {
            throw  new IllegalArgumentException("invalid depth level. Must be in range (1, " + totalDepthLevels + ")");
        }
    }

    public void setWeight(int weight) {
        ValidateUtil.validatePositiveNumbers(weight);
        this.weight = weight;
    }

    public void setBaseSpeed(float baseSpeed) {
        this.baseSpeed = Math.abs(baseSpeed);

    }
}
