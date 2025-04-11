package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.List;
public class TraderComponent implements Component {
    private boolean renderTrader;
    private List<Entity> products = new ArrayList<>();

    public void addProduct(Entity product) {
        if (!products.contains(product)) {
            products.add(product);
        }
    }

    public List<Entity> getProducts() {
        return products;
    }
    public boolean isRenderTrader() {
        return renderTrader;
    }

    public void setRenderTrader(boolean renderTrader) {
        this.renderTrader = renderTrader;
    }
}
