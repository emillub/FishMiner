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
import com.github.FishMiner.infrastructure.Configuration;
import com.github.FishMiner.infrastructure.Logger;
import com.github.FishMiner.infrastructure.ValidateUtil;
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
import com.github.FishMiner.ui.ports.out.domain.events.TransactionEvent;
import com.github.FishMiner.domain.ecs.entities.playerFactory.PlayerFactory;
import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameEventListener;
import com.github.FishMiner.domain.ecs.states.TraderStates;


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
    public void update(float delta) {
        // Update trader's position/state.
        TransformComponent traderPos = transMapper.get(trader);
        StateComponent<TraderStates> traderState = stm.get(trader);
        ValidateUtil.validateMultipleNotNull(traderPos, "traderPos", traderState, "traderState");
        traderPos.pos.set(
            Configuration.getInstance().getScreenWidth() * 0.5f,
            Configuration.getInstance().getScreenHeight() * 0.5f,
            2
        );

        // Process each pending transaction.
        for (int i = 0; i < transactionQueue.size; i++) {
            TransactionEntry entry = transactionQueue.get(i);
            Entity playerEntity = entry.buyer;
            Entity upgradeEntity = entry.upgradeItem;

            // Retrieve required components.
            ScoreComponent playerScore = scm.get(playerEntity);
            UpgradeComponent upgrade = upgradeMapper.get(upgradeEntity);
            ValidateUtil.validateMultipleNotNull(playerScore, "playerScore", upgrade, "upgrade");
            TransformComponent upgradePos = transMapper.get(upgradeEntity);
            ValidateUtil.validateNotNull(upgradePos, "upgradePos");

            // Define target position for thrown upgrade (off the left side).
            Configuration config = Configuration.getInstance();
            Vector3 target = new Vector3(-50, upgradePos.pos.y, upgradePos.pos.z);
            Vector3 start = new Vector3(upgradePos.pos);
            float t = entry.flyTime / FLY_DURATION;
            t = Math.min(t, 1f);
            float x = start.x + t * (target.x - start.x);
            float y = start.y + t * (target.y - start.y) + ARC_HEIGHT * (1 - t) * t;
            upgradePos.pos.set(x, y, target.z);
            entry.flyTime += delta;

            // Set trader state to trigger the BUY (throwing) animation.
            traderState.changeState(TraderStates.BUY);

            // Finalize the transaction when flight animation is complete.
            if (entry.flyTime >= FLY_DURATION && !entry.transactionCompleted) {
                PlayerComponent player = pm.get(playerEntity);
                InventoryComponent playerInventory = invMapper.get(playerEntity);
                ValidateUtil.validateNotNull(player, "player");

                // For Reel upgrades.
                if (upgradeEntity.getComponent(ReelComponent.class) != null) {
                    Entity currentlyEquipped = player.getReel();
                    ReelComponent currentlyEquippedReel = currentlyEquipped.getComponent(ReelComponent.class);
                    ValidateUtil.validateNotNull(currentlyEquippedReel, "currentlyEquippedReel");
                    playerInventory.addReelToInventory(currentlyEquippedReel.getName(), currentlyEquipped);

                    player.setReel(upgradeEntity);

                    TraderComponent traderComp = tm.get(trader);
                    if (traderComp != null) {
                        traderComp.getProducts().remove(upgradeEntity);
                    }
                    entry.transactionCompleted = true;
                    upgrade.setUpgraded(true);
                }
                // Sinker upgrades
                else if (upgradeEntity.getComponent(SinkerComponent.class) != null) {
                    Entity currentlyEquipped = player.getSinker();
                    ReelComponent currentlyEquippedSinker = currentlyEquipped.getComponent(ReelComponent.class);
                    ValidateUtil.validateNotNull(currentlyEquippedSinker, "currentlyEquippedSinker");

                    player.setSinker(upgradeEntity);
                    playerInventory.addSinkerToInventory(currentlyEquippedSinker.getName(), currentlyEquipped);

                    TraderComponent traderComp = tm.get(trader);
                    if (traderComp != null) {
                        traderComp.getProducts().remove(upgradeEntity);
                    }
                    entry.transactionCompleted = true;
                    upgrade.setUpgraded(true);
                }
                // Hook upgrades
                else if (upgradeEntity.getComponent(HookComponent.class) != null) {
                    Entity currentlyEquipped = player.getHook();
                    HookComponent currentHookComp = currentlyEquipped.getComponent(HookComponent.class);
                    playerInventory.addHooksToInventory(currentHookComp.getName(), currentlyEquipped);

                    player.setHook(upgradeEntity);
                    playerInventory.equipHook(upgradeEntity.getComponent(HookComponent.class).getName());

                    TraderComponent traderComp = tm.get(trader);
                    if (traderComp != null) {
                        traderComp.getProducts().remove(upgradeEntity);
                    }
                    entry.transactionCompleted = true;
                    upgrade.setUpgraded(true);
                }


                transactionQueue.removeIndex(i--); // Remove the entry and adjust the loop.
            }
        }
        if (transactionQueue.size == 0) {
            setProcessing(false);
        }
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
