package com.github.FishMiner.domain;

import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.ReelComponent;
import com.github.FishMiner.domain.ecs.components.SinkerComponent;
import com.github.FishMiner.domain.ecs.components.TraderComponent;
import com.github.FishMiner.domain.ecs.components.UpgradeComponent;
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

    public void resetStore() {
        Entity[] entities = TraderFactory.createNewUpgrades(engine, 0, 0);
        TraderComponent traderComponent = trader.getComponent(TraderComponent.class);
        traderComponent.getProducts().forEach(product -> {
            // System.out.println("PRODUCT");
            // UpgradeComponent upgradeComponent =
            // product.getComponent(UpgradeComponent.class);
            // if (product.getComponent(HookComponent.class) != null) {
            // System.out.println("Checking : " +
            // product.getComponent(HookComponent.class).name);
            // }
            // if (product.getComponent(ReelComponent.class) != null) {
            // System.out.println("Checking : " +
            // product.getComponent(ReelComponent.class).name);
            // }
            // if (product.getComponent(SinkerComponent.class) != null) {
            // System.out.println("Checking : " +
            // product.getComponent(SinkerComponent.class).name);
            // }

            // if (upgradeComponent == null) {
            // System.out.println("No upgrade component found, creating a new one.");
            // upgradeComponent = new UpgradeComponent();
            // upgradeComponent.setPrice(2000);
            // product.add(upgradeComponent);
            // } else {
            // System.out.println("Is it upgraded : " + upgradeComponent.isUpgraded());
            // if (upgradeComponent.isUpgraded()) {
            // System.out.println("Resetting : " + upgradeComponent.getType());
            // upgradeComponent.setUpgraded(false);
            // }
            // }
            product.removeAll();
            // engine.addEntity(product);
            engine.removeEntity(product);
        });

        traderComponent.getProducts().clear();
        for (Entity entity : entities) {
            trader.getComponent(TraderComponent.class).addProduct(entity);
            if (entity.getComponent(HookComponent.class) != null) {
                System.out.println(entity.getComponent(HookComponent.class).name);
            }
            if (entity.getComponent(ReelComponent.class) != null) {
                System.out.println(entity.getComponent(ReelComponent.class).name);
            }
            if (entity.getComponent(SinkerComponent.class) != null) {
                System.out.println(entity.getComponent(SinkerComponent.class).name);
            }
        }
        traderComponent.getProducts().forEach(product -> {
            System.out.println("PRODUCT");
            UpgradeComponent upgradeComponent = product.getComponent(UpgradeComponent.class);
            if (product.getComponent(HookComponent.class) != null) {
                System.out.println("Checking : " + product.getComponent(HookComponent.class).name);
            }
            if (product.getComponent(ReelComponent.class) != null) {
                System.out.println("Checking : " + product.getComponent(ReelComponent.class).name);
            }
            if (product.getComponent(SinkerComponent.class) != null) {
                System.out.println("Checking : " + product.getComponent(SinkerComponent.class).name);
            }

            if (upgradeComponent == null) {
                System.out.println("No upgrade component found, creating a new one.");
                upgradeComponent = new UpgradeComponent();
                upgradeComponent.setPrice(2000);
                product.add(upgradeComponent);
            } else {
                System.out.println("Is it upgraded : " + upgradeComponent.isUpgraded());
                if (upgradeComponent.isUpgraded()) {
                    System.out.println("Resetting : " + upgradeComponent.getType());
                    upgradeComponent.setUpgraded(false);
                }
            }
        });
        System.out.println("New product size: " + products.size());

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
