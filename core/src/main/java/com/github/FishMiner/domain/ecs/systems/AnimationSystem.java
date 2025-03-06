package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.states.IState;


public class AnimationSystem extends IteratingSystem {

    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);

    public AnimationSystem() {
        super(Family.all(AnimationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animationComponent = am.get(entity);
        StateComponent<?> stateComponent = sm.get(entity);

        if (animationComponent != null && stateComponent != null && stateComponent.state instanceof IState) {
            IState animationState = stateComponent.state;

            animationComponent.timer += deltaTime;

            // Get animation key from the current state
            String animationKey = animationState.getAnimationKey();

            // Change animation if different from current
            if (!animationComponent.currentAnimation.equals(animationComponent.animations.get(animationKey))) {
                animationComponent.setCurrentAnimation(animationKey);
            }
        }
    }
}
