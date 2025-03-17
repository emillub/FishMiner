package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.util.HookUtil;
import com.github.FishMiner.domain.events.IGameEventListener;
import com.github.FishMiner.domain.events.impl.FireHookEvent;
import com.github.FishMiner.domain.states.HookStates;

public class HookInputSystem extends IteratingSystem implements IGameEventListener<FireHookEvent> {

    private ComponentMapper<HookComponent> hookMapper = ComponentMapper.getFor(HookComponent.class);
    private Entity hook;

    public HookInputSystem() {
        super(Family.all(HookComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        // Get the hook entity from the engine (ensure HookUtil works correctly)
        hook = HookUtil.getHook(engine);
        if (hook == null) {
            throw new IllegalArgumentException("Hook cannot be null");
        }
    }

    @Override
    public void onEvent(FireHookEvent event) {
        // Fire the hook only if its state is SWINGING
        Entity hookEntity = event.getEventEntity();
        StateComponent<HookStates> stateComponent = hookEntity.getComponent(StateComponent.class);
        if (stateComponent != null && stateComponent.state == HookStates.SWINGING) {
            stateComponent.changeState(HookStates.FIRE);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // No per-frame processing needed in this system; it is event-driven.
    }
}
