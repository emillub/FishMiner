package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.states.IState;

@SuppressWarnings("unchecked")
public class AnimationSystem extends IteratingSystem {

    private final ComponentMapper<AnimationComponent> animationMapper = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<StateComponent> stateMapper = ComponentMapper.getFor(StateComponent.class);

    public AnimationSystem() {
        super(Family.all(StateComponent.class, AnimationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animationComponent = animationMapper.get(entity);
        StateComponent<? extends IState> stateComponent = (StateComponent<? extends IState>) stateMapper.get(entity);

        if (animationComponent == null || stateComponent == null) return;

        IState currentState = stateComponent.getState();
        if (currentState == null) return;

        String newAnimKey = currentState.getAnimationKey();
        animationComponent.increaseTimer(deltaTime);

        if (!newAnimKey.equals(animationComponent.getCurrentAnimationKey())) {
            animationComponent.setCurrentAnimation(newAnimKey);
        }
    }
}
