package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.ReelComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.states.IState;

@SuppressWarnings("unchecked")
public class AnimationSystem extends IteratingSystem {
    private static final String TAG = "AnimationSystem";

    private final ComponentMapper<AnimationComponent> animationMapper = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<StateComponent> stateMapper = ComponentMapper.getFor(StateComponent.class);

    public AnimationSystem() {
        super(Family.all(StateComponent.class, AnimationComponent.class)
            .exclude(ReelComponent.class)
            .get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animationComponent = animationMapper.get(entity);
        StateComponent<? extends IState> stateComponent = (StateComponent<? extends IState>) stateMapper.get(entity);

        if (animationComponent != null && stateComponent != null) {
            animationComponent.timer += deltaTime;

            String newAnimKey = stateComponent.getState().getAnimationKey();

            // Switch animation only if needed
            if (!newAnimKey.equals(animationComponent.getCurrentAnimationKey())) {
                if (!animationComponent.animations.containsKey(newAnimKey)) {
                    Logger.getInstance().error(TAG,
                        "❌ Tried to switch to unknown animation key: '" + newAnimKey + "' on entity: " + entity);
                    return;
                }
                if (stateComponent.getState() == null) {
                    Logger.getInstance().error(TAG, "⚠️ Entity " + entity + " has null state in StateComponent!");
                    return;
                }

                System.out.println("[AnimationSystem] 🔁 Switching animation to: " + newAnimKey +
                    " | Entity: " + entity +
                    " | State class: " + stateComponent.getState().getClass().getSimpleName());
                animationComponent.setCurrentAnimation(newAnimKey);
            }

        } else if (animationComponent != null) {
            // Continue time if no state available (shouldn't usually happen)
            animationComponent.timer += deltaTime;
        }
    }
}
