package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Family;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.ReelComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.states.HookStates;

public class ReelAnimationSystem extends IteratingSystem {

    private final ComponentMapper<ReelComponent> reelMapper = ComponentMapper.getFor(ReelComponent.class);
    private final ComponentMapper<StateComponent> stateMapper = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<AnimationComponent> animationMapper = ComponentMapper.getFor(AnimationComponent.class);

    public ReelAnimationSystem() {
        super(Family.all(ReelComponent.class, StateComponent.class, AnimationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent<HookStates> stateComp = stateMapper.get(entity);
        AnimationComponent animComp = animationMapper.get(entity);

        String stateKey = stateComp.getState().getAnimationKey();

        if (!stateKey.equals(animComp.getCurrentAnimationKey())) {
            animComp.setCurrentAnimation(stateKey);
        }

        animComp.timer += deltaTime;
    }
}
