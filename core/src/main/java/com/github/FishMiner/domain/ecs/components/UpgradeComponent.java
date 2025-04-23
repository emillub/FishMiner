package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.infrastructure.Logger;
import com.github.FishMiner.infrastructure.ValidateUtil;

public class UpgradeComponent implements Component {
    private final static String TAG = "UpgradeComponent";
    private boolean isUpgraded = false;
    private int price = -1;
    private Entity type;

    public void setUpgraded(boolean isUpgraded) {
        this.isUpgraded = isUpgraded;
    }

    public void setType(Entity type) {
        this.type = type;
    }

    public void setPrice(int upgradePrice) {
        ValidateUtil.validatePositiveInt(upgradePrice, "upgradePrice");
        this.price = upgradePrice;
    }


    public Entity getType() {
        return type;
    }

    public int getPrice() {
        if (price == -1) {
            IllegalStateException exception = new IllegalStateException("Upgrade price must be set");
            Logger.getInstance().error(TAG, "Cannot return the price for UpgradeComponent", exception);
            throw exception;
        }
        return price;
    }

    public boolean isUpgraded() {
        return this.isUpgraded;
    }
}
