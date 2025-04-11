package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
import com.github.FishMiner.domain.ecs.components.ScoreComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TraderComponent;
import com.github.FishMiner.domain.ecs.components.UpgradeComponent;
import com.github.FishMiner.domain.events.ecsEvents.TransactionEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;


public class TraderSystem extends EntitySystem implements IGameEventListener<TransactionEvent> {
    private static final String TAG = "TraderSystem";
    ComponentMapper<TraderComponent> tm = ComponentMapper.getFor(TraderComponent.class);
    ComponentMapper<StateComponent> stm = ComponentMapper.getFor(StateComponent.class);
    ComponentMapper<ScoreComponent> scm = ComponentMapper.getFor(ScoreComponent.class);
    ComponentMapper<PlayerComponent> pm = ComponentMapper.getFor(PlayerComponent.class);

    private final Array<TransactionEntry> transactionQueue = new Array<>();

    public TraderSystem() {
        setProcessing(false);
    }
    private static class TransactionEntry {
        Entity buyer;
        Entity upgradeItem;
        float flyTime;
        boolean transactionCompleted;
    }

    @Override
    public void setProcessing(boolean processing) {
        super.setProcessing(processing);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void onEvent(TransactionEvent event) {
        setProcessing(true);
        if (event.isHandled()) return;

        Entity player = event.getSource();
        Entity selectedUpgrade = event.getTarget();

        try {
            ValidateUtil.validateMultipleNotNull(player, selectedUpgrade);
            transactionQueue.add(new TransactionEntry() {{
                this.buyer = player;
                this.upgradeItem = selectedUpgrade;
                this.flyTime = 0f;
                this.transactionCompleted = false;
            }});
            event.setHandled();
        } catch (IllegalArgumentException e) {
            Logger.getInstance().error(TAG, "Invalid capture: " + e.getMessage());
        }
    }

    @Override
    public Class<TransactionEvent> getEventType() {
        return TransactionEvent.class;
    }
}
