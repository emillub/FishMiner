package com.github.FishMiner.domain;

import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.systems.TraderSystem;
import com.github.FishMiner.domain.events.ecsEvents.TransactionEvent;
import com.github.FishMiner.domain.factories.playerFactory.TraderFactory;
import com.github.FishMiner.domain.shop.UpgradeItem;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all available upgrade items in the game.
 */
public class UpgradeStore {
    private static final String TAG = "UpgradeStore";
    private static UpgradeStore instance;
    private final PooledEngine engine;
    private TransactionEvent transactionEvent;
    private Entity trader;
    private TraderFactory traderFactory;

    public UpgradeStore(PooledEngine engine) {
        this.engine = engine;
        TraderFactory.addNewTraderTo(engine,
            (int) (Configuration.getInstance().getScreenWidth() * 0.5f),
            (int) (Configuration.getInstance().getScreenWidth() * 0.5f)
        );
        trader = TraderFactory.getTraderEntity();
        ValidateUtil.validateNotNull(trader, "PlayerCharacter -> playerEntity");
    }

    public static UpgradeStore getInstance(PooledEngine engine) {
        if (instance == null) {
            instance = new UpgradeStore(engine);
        }
        return instance;
    }

    protected void update(float deltaTime) {
        engine.getSystem(TraderSystem.class).update(deltaTime);
    }

    public static Map<String, UpgradeItem> getAvailableUpgrades() {
        Map<String, UpgradeItem> store = new HashMap<>();
        store.put("long_reel", new UpgradeItem(
            "Long_reel",
            "Long Reel",
            "Reach deeper fish",
            10,
            "red"
        ));
        return store;
    }
}
