package com.github.FishMiner.domain;

import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.components.TraderComponent;
import com.github.FishMiner.domain.factories.playerFactory.TraderFactory;
import java.util.List;
import com.badlogic.ashley.core.Entity;

/**
 * The UpgradeStore now works more like the World.
 * An instance is created (and stored in GameContext) and can be used
 * by screens to get the available upgrade products.
 */
public class UpgradeStore {
    private static UpgradeStore instance;
    private final PooledEngine engine;
    private final Entity trader;
    private final List<Entity> products;

    public UpgradeStore(PooledEngine engine) {
        this.engine = engine;
        // Create the trader and add products (for example, a heavy sinker).
        TraderFactory.addNewTraderTo(engine,
            (int) (Configuration.getInstance().getScreenWidth() * 0.5f),
            (int) (Configuration.getInstance().getScreenWidth() * 0.5f)
        );
        trader = TraderFactory.getTraderEntity();
        ValidateUtil.validateNotNull(trader, "Trader must not be null");
        TraderComponent traderComponent = trader.getComponent(TraderComponent.class);
        products = traderComponent.getProducts();
    }

    public static UpgradeStore getInstance(PooledEngine engine) {
        if (instance == null) {
            instance = new UpgradeStore(engine);
        }
        return instance;
    }

    // Exposes the list of upgrade (product) entities.
    public List<Entity> getUpgradeProducts() {
        return products;
    }

    // Controls whether the trader is rendered onscreen.
    public void setRenderTrader(boolean isRender) {
        trader.getComponent(TraderComponent.class).setRenderTrader(isRender);
    }

    // Optionally, you might add an update method (similar to World.update)
    public void update(float deltaTime) {
        engine.update(deltaTime);
    }

    public Entity getTraderEntity() {
        return this.trader;
    }
}
