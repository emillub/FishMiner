package com.github.FishMiner.ui.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.ecsEvents.TransactionEvent;
import com.github.FishMiner.domain.ecs.components.SinkerComponent;
import com.github.FishMiner.domain.ecs.components.ReelComponent;
import com.github.FishMiner.domain.ecs.components.UpgradeComponent;
import com.github.FishMiner.domain.UpgradeStore;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.ui.factories.ButtonFactory;
import com.github.FishMiner.ui.ports.in.IPlayer;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;
import java.util.List;

public class UpgradeScreen extends AbstractScreen implements IGameScreen {
    private final List<Entity> upgradeProducts;
    private final IPlayer player;
    private final UpgradeStore upgradeStore;

    public UpgradeScreen(IGameContext gameContext) {
        super(gameContext);
        screenType = ScreenType.UPGRADE;
        player = gameContext.getPlayer();
        // Retrieve the upgrade store from GameContext (which now holds an instance of UpgradeStore)
        this.upgradeStore = gameContext.getUpgradeStore();
        this.upgradeProducts = upgradeStore.getUpgradeProducts();
    }

    @Override
    public void show() {
        super.show();
        // Ensure the trader (and its upgrades) are rendered
        upgradeStore.setRenderTrader(true);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(40);
        stage.addActor(table);

        Label title = new Label("Upgrade Store", skin);
        title.setFontScale(2f);
        table.add(title).padBottom(30).row();

        final Label moneyLabel = new Label("Available money: " + player.getScore(), skin);
        moneyLabel.setFontScale(1.3f);
        table.add(moneyLabel).padBottom(30).row();

        // Iterate over each upgrade product available in the store.
        for (Entity product : upgradeProducts) {
            String productName = "";
            int productPrice = 0;

            // Determine the display name from attached components.
            if (product.getComponent(SinkerComponent.class) != null) {
                productName = product.getComponent(SinkerComponent.class).name;
            } else if (product.getComponent(ReelComponent.class) != null) {
                productName = product.getComponent(ReelComponent.class).getName();
            } else if (product.getComponent(HookComponent.class) != null) {
                productName = product.getComponent(HookComponent.class).getName();
            }
            // Get the cost from the UpgradeComponent.
            productPrice = product.getComponent(UpgradeComponent.class).getPrice();

            Label itemLabel = new Label(productName + " (" + productPrice + ")", skin);
            itemLabel.setFontScale(1.1f);
            String finalProductName = productName;

            // Use ButtonFactory to create the buy button.
            final TextButton buyButton = ButtonFactory.createTextButton("Buy",
                    ButtonFactory.ButtonSize.SMALL, () -> {
                        TransactionEvent txEvent = new TransactionEvent(player.getPlayerEntity(), product);
                        GameEventBus.getInstance().post(txEvent);
                    });

            // When the buy button is pressed, create and post a TransactionEvent.

            Table itemRow = new Table();
            itemRow.add(itemLabel).left().expandX().padRight(15);
            itemRow.add(buyButton).size(buyButton.getWidth() + Configuration.getInstance().getSmallPadding(),
                    Configuration.getInstance().getSmallPadding()).padLeft(15);

            table.add(itemRow).padBottom(20).fillX().width(400).row();
        }

        TextButton continueButton = ButtonFactory.createTextButton("Continue",
                ButtonFactory.ButtonSize.SMALL, () -> {
                    // Optionally hide the upgrade trader when continuing.
                    upgradeStore.setRenderTrader(false);
                    ScreenManager.getInstance().startNextLevel();
                });

        table.add(continueButton).padTop(40).width(240).height(70).colspan(2).center().row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        super.drawBackground();
        stage.act(delta);
        stage.draw();
    }
}
