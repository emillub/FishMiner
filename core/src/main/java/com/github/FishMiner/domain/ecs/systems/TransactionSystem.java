package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
import com.github.FishMiner.domain.ecs.components.ReelComponent;
import com.github.FishMiner.domain.ecs.components.ScoreComponent;
import com.github.FishMiner.domain.ecs.components.SinkerComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TraderComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.UpgradeComponent;
import com.github.FishMiner.domain.ecs.utils.DomainUtils;
import com.github.FishMiner.domain.events.ecsEvents.TransactionEvent;
import com.github.FishMiner.domain.factories.ReelTypes;
import com.github.FishMiner.domain.factories.playerFactory.PlayerFactory;
import com.github.FishMiner.domain.factories.playerFactory.ReelFactory;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.states.TraderStates;


public class TransactionSystem extends EntitySystem implements IGameEventListener<TransactionEvent> {
    private static final String TAG = "TraderSystem";
    ComponentMapper<TraderComponent> tm = ComponentMapper.getFor(TraderComponent.class);
    ComponentMapper<StateComponent> stm = ComponentMapper.getFor(StateComponent.class);
    ComponentMapper<ScoreComponent> scm = ComponentMapper.getFor(ScoreComponent.class);
    ComponentMapper<PlayerComponent> pm = ComponentMapper.getFor(PlayerComponent.class);
    ComponentMapper<TransformComponent> transMapper = ComponentMapper.getFor(TransformComponent.class);
    ComponentMapper<UpgradeComponent> upgradeMapper = ComponentMapper.getFor(UpgradeComponent.class);
    ComponentMapper<InventoryComponent> invMapper = ComponentMapper.getFor(InventoryComponent.class);

    Engine engine;
    Entity trader;
    private static final float FLY_DURATION = 1.0f;
    private static final float ARC_HEIGHT = 50f;
    private final Array<TransactionEntry> transactionQueue = new Array<>();

    public TransactionSystem() {
        setProcessing(false);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        ImmutableArray<Entity> traderArr = engine.getEntitiesFor(Family.all(TraderComponent.class, StateComponent.class).get());
        this.trader = traderArr.first();
        this.engine = engine;
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
    public void onEvent(TransactionEvent event) {
        setProcessing(true);
        if (event == null || event.isHandled()) {
            Logger.getInstance().log(TAG, "Event is null or already handled");
            return;
        }
        Entity player = event.getSource();
        Entity selectedUpgrade = event.getTarget();
        ScoreComponent playerScore = player.getComponent(ScoreComponent.class);
        UpgradeComponent upgradePrice = selectedUpgrade.getComponent(UpgradeComponent.class);
        if (playerScore.getScore() < upgradePrice.getPrice()) {
            event.setApproved(false);
            event.setHandled();
            return;
        }
        playerScore.setScore(playerScore.getScore() - upgradePrice.getPrice());
        upgradePrice.setUpgraded(true);

        if (selectedUpgrade.getComponent(ReelComponent.class) != null) {
            PlayerFactory.updateReel(selectedUpgrade, player, (PooledEngine) engine);
        } else if (selectedUpgrade.getComponent(SinkerComponent.class) != null) {
            PlayerFactory.updateSinker(selectedUpgrade, player, (PooledEngine) engine);
        } else if (selectedUpgrade.getComponent(HookComponent.class) != null) {
            PlayerFactory.updateHook(selectedUpgrade, player, (PooledEngine) engine);
        } else {
            Logger.getInstance().log(TAG, "Selected upgrade is not a valid upgrade");
            event.setApproved(false);
            event.setHandled();
            return;
        }

    }
    @Override
    public Class<TransactionEvent> getEventType() {
        return TransactionEvent.class;
    }
}
