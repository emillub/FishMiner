package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.util.HookUtil;
import com.github.FishMiner.domain.events.IEventListener;
import com.github.FishMiner.domain.events.impl.FireHookEvent;
import com.github.FishMiner.domain.states.EntityState;


public class HookInputSystem extends EntitySystem implements IEventListener<FireHookEvent> {
    ComponentMapper<HookComponent> hm = ComponentMapper.getFor(HookComponent.class);
    private Entity hook;

    @Override
    public void onEvent(FireHookEvent event) {
        Entity hook = event.getEventEntity();
        StateComponent<EntityState.HookStates> stateComponent = hook.getComponent(StateComponent.class);
        if (stateComponent.state == EntityState.HookStates.SWINGING) {
            stateComponent.changeState(EntityState.HookStates.FIRE);
        }
    }
    @Override
    public void addedToEngine(Engine engine) {
        hook = HookUtil.getHook(engine);
        if (hook == null) {
            throw new IllegalArgumentException("Hook cannot be null");
        }
    }
}
