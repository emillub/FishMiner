package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.domain.ecs.components.TraderComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.UpgradeComponent;

public class StoreSystem extends EntitySystem {
    private final ComponentMapper<TraderComponent> traderMapper = ComponentMapper.getFor(TraderComponent.class);
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
    private ImmutableArray<Entity> upgradeEntities;
    private Entity trader;

    @Override
    public void addedToEngine(Engine engine) {
        // Look up the trader (requires that it has both TraderComponent and TransformComponent).
        trader = engine.getEntitiesFor(Family.all(TraderComponent.class, TransformComponent.class).get()).first();
        // All upgrade entities, which also have a TransformComponent.
        upgradeEntities = engine.getEntitiesFor(Family.all(UpgradeComponent.class, TransformComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        // Get the trader's component.
        TraderComponent traderComp = traderMapper.get(trader);

        // Check the render flag. The flag should be set to true when in the upgrade screen.
        if (!traderComp.isRenderTrader()) {
            // When not in the upgrade screen, hide the trader by moving it off-screen.
            TransformComponent traderTransform = transformMapper.get(trader);
            traderTransform.pos.set(-1000, -1000, 0);
            // Also hide each upgrade entity.
            for (Entity upgrade : upgradeEntities) {
                TransformComponent upgradeTrans = transformMapper.get(upgrade);
                upgradeTrans.pos.set(-1000, -1000, 0);
            }
            return;
        }

        TransformComponent traderTransform = transformMapper.get(trader);
        traderTransform.pos.set(
            Configuration.getInstance().getScreenWidth() * 0.5f,
            100,
            0
        );

        // Store display
        int columns = 3;
        float startX = 100;  // left margin
        float startY = Configuration.getInstance().getScreenHeight() - 150;  // top margin
        float paddingX = 150; // horizontal spacing
        float paddingY = 150; // vertical spacing

        int index = 0;
        for (Entity upgrade : upgradeEntities) {

            TransformComponent upgradeTrans = transformMapper.get(upgrade);

            if (upgradeTrans == null) {
                upgradeTrans = getEngine().createComponent(TransformComponent.class);
                upgrade.add(upgradeTrans);
            }

            int col = index % columns;
            int row = index / columns;
            float x = startX + col * paddingX;
            float y = startY - row * paddingY;
            upgradeTrans.pos.set(x, y, 0);
            index++;
        }
    }
}
