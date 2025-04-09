package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.util.HookUtil;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;
import com.github.FishMiner.domain.events.impl.FireInputEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.states.HookStates;

public class HookInputSystem extends EntitySystem implements IGameEventListener<FireInputEvent> {
    private static final String TAG = "HookInputSystem";
    private Entity hook;

    public HookInputSystem() {
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onEvent(FireInputEvent event) {
        if (!event.isHandled()) {
            Entity hookEntity = event.getTarget();
            ValidateUtil.validateNotNull(hookEntity,  TAG + " -> hookEntity");
            StateComponent<HookStates> stateComponent =  hookEntity.getComponent(StateComponent.class);
            ValidateUtil.validateNotNull(stateComponent, TAG + " -> StateComponent");
            if (stateComponent.state == HookStates.SWINGING) {
                stateComponent.changeState(HookStates.FIRE);
            }
            event.setHandled();
        }
    }

    @Override
    public Class<FireInputEvent> getEventType() {
        return FireInputEvent.class;
    }
}
