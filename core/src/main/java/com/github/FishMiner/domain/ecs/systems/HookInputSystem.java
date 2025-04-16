package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.events.ecsEvents.HookInputEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.states.FishingRodState;

public class HookInputSystem extends EntitySystem implements IGameEventListener<HookInputEvent> {
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
    public void onEvent(HookInputEvent event) {
        if (!event.isHandled()) {
            Entity hookEntity = event.getTarget();
            ValidateUtil.validateNotNull(hookEntity,  TAG + " -> hookEntity");
            StateComponent<FishingRodState> stateComponent =  hookEntity.getComponent(StateComponent.class);
            ValidateUtil.validateNotNull(stateComponent, TAG + " -> StateComponent");
            if (stateComponent.state == FishingRodState.SWINGING) {
                stateComponent.changeState(FishingRodState.FIRE);
            }
            event.setHandled();
        }
    }

    @Override
    public Class<HookInputEvent> getEventType() {
        return HookInputEvent.class;
    }
}
