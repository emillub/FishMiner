package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.util.HookUtil;
import com.github.FishMiner.domain.events.GameEventBus;
import com.github.FishMiner.domain.events.impl.FireInputEvent;
import com.github.FishMiner.domain.listeners.IGameEventListener;
import com.github.FishMiner.domain.states.HookStates;

public class HookInputSystem extends EntitySystem implements IGameEventListener<FireInputEvent> {

    private ComponentMapper<HookComponent> hookMapper = ComponentMapper.getFor(HookComponent.class);
    private Entity hook;

    public HookInputSystem() {
        GameEventBus.getInstance().register(this);
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
    public void onEvent(FireInputEvent event) {
        // Fire the hook only if its state is SWINGING
        Entity hookEntity = event.getEventEntity();
        StateComponent<HookStates> stateComponent = hookEntity.getComponent(StateComponent.class);
        if (stateComponent != null && stateComponent.state == HookStates.SWINGING) {
            stateComponent.changeState(HookStates.FIRE);
        }
    }

    @Override
    public Class<FireInputEvent> getEventType() {
        return FireInputEvent.class;
    }
}
