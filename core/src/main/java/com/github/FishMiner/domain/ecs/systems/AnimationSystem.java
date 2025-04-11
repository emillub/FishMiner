package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.states.IState;

@SuppressWarnings("unchecked")
public class AnimationSystem extends IteratingSystem {
    private static final String TAG = "AnimationSystem";

    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);

    public AnimationSystem() {
        super(Family.all(AnimationComponent.class).get());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animationComponent = am.get(entity);
        //StateComponent stateComponent = sm.get(entity);
        StateComponent<? extends IState> stateComponent = (StateComponent<? extends IState>) sm.get(entity);

        if (animationComponent != null && stateComponent != null) {
            // Get animation key from the current state
            String currentStateAnimKey = stateComponent.state.getAnimationKey();

            animationComponent.timer += deltaTime;

            // Change animation if different from current
            if (!animationComponent.getCurrentAnimationKey().equals(currentStateAnimKey)) {
                if (!animationComponent.animations.containsKey(currentStateAnimKey)) {
                    Logger.getInstance().error(TAG, "Tried to set animation with incorrect/missing key");
                }
                animationComponent.setCurrentAnimation(currentStateAnimKey);
            }
        } else if (animationComponent != null) {
            animationComponent.timer += deltaTime;
        }
    }
}
