package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

/**
 * The {@code AnimationComponent} class is responsible for storing and managing
 * animations for an entity. It maintains a collection of animations keyed by a state identifier,
 * a reference to the current animation, and a timer to track the elapsed time for the animation.
 * <p>
 * This component works in conjunction with a {@code TextureComponent} to split a texture into frames
 * and create animations for various entity states.
 * </p>
 */
public class AnimationComponent implements Component {

    /**
     * A map of animations associated with their corresponding state keys.
     */
    public HashMap<String, Animation<TextureRegion>> animations = new HashMap<>();

    /**
     * The current active animation for the entity.
     */
    public Animation<TextureRegion> currentAnimation;
    public String currentAnimationKey;

    /**
     * Timer to track the elapsed time used for animation frame selection.
     */
    public float timer = 0f;

    /**
     * Adds an animation to the component using a specific key. This method splits the provided
     * texture into frames based on the given state row, creates an animation with a fixed frame duration,
     * and assigns a play mode to control whether the animation loops.
     *
     * @param key              A String representing the state of the animation (e.g., "SWIMMING", "HOOKED").
     * @param textureComponent The {@code TextureComponent} containing the texture region and frame configuration.
     * @param stateRow         The row index in the split texture that contains the frames for this animation.
     * @param playMode         The play mode to be applied to the animation (e.g., {@code Animation.PlayMode.LOOP} or {@code Animation.PlayMode.NORMAL}).
     */
    public void addAnimation(String key, TextureComponent textureComponent, int stateRow, Animation.PlayMode playMode) {
        TextureRegion region = textureComponent.getRegion();
        int FRAME_COLS = textureComponent.getFrameCols();
        int FRAME_ROWS = textureComponent.getFrameRows();

        // Split the texture into a 2D array of regions
        TextureRegion[][] tmp = TextureRegion.split(
            region.getTexture(),
            region.getRegionWidth() / FRAME_COLS,
            region.getRegionHeight() / FRAME_ROWS
        );

        // Extract the frames from the specified row for the animation
        TextureRegion[] animationFrames = new TextureRegion[FRAME_COLS];
        int index = 0;
        for (int i = 0; i < FRAME_COLS; i++) {
            animationFrames[index++] = tmp[stateRow][i];
        }

        // Create the animation with a fixed frame duration of 0.1 seconds
        Animation<TextureRegion> animation = new Animation<>(0.1f, animationFrames);
        // Set the play mode (e.g., looping or normal)
        animation.setPlayMode(playMode);

        // Store the animation with the specified key
        animations.put(key, animation);
        // If no current animation is set, assign this animation as the current one
        if (currentAnimation == null) {
            currentAnimation = animation;
        }
    }

    /**
     * Adds an animation with the default play mode of {@code Animation.PlayMode.LOOP}.
     *
     * @param key              A String representing the state of the animation.
     * @param textureComponent The {@code TextureComponent} containing the texture region and frame configuration.
     * @param stateRow         The row index in the split texture that contains the frames for this animation.
     */
    public void addAnimation(String key, TextureComponent textureComponent, int stateRow) {
        this.addAnimation(key, textureComponent, stateRow, Animation.PlayMode.LOOP);
    }

    /**
     * Sets the current animation of the component to the animation associated with the provided key.
     * Also resets the animation timer to start the animation from the beginning.
     *
     * @param key A String representing the state of the animation to be set as current.
     */
    public void setCurrentAnimation(String key) {
        if (animations.containsKey(key)) {
            currentAnimation = animations.get(key);
            currentAnimationKey = key;
            timer = 0f;
        }
    }
    public String getCurrentAnimationKey() {
        return currentAnimationKey != null ? currentAnimationKey : "UNKNOWN";
    }
}
