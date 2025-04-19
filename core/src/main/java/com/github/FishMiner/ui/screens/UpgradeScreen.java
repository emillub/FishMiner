package com.github.FishMiner.ui.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.common.Assets;
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

import java.io.ObjectInputFilter.Config;
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
        Gdx.input.setInputProcessor(stage);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Label titleLabel = new Label("Upgrade Store", skin);
        titleLabel.setFontScale(Configuration.getInstance().getLargeFontScale());
        rootTable.add(titleLabel).expandX().top().padTop(Configuration.getInstance().getLargePadding())
                .padBottom(Configuration.getInstance().getLargePadding()).row();

        Table productTable = new Table();

        ScrollPane scrollPane = new ScrollPane(productTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        rootTable.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.2f))); // Transparent white background

        rootTable.add(scrollPane)
                .expandY()
                .fillY()
                .row();

        for (Entity product : upgradeProducts) {
            String productName = "";
            int productPrice = 0;

            if (product.getComponent(SinkerComponent.class) != null) {
                productName = product.getComponent(SinkerComponent.class).name;
            } else if (product.getComponent(ReelComponent.class) != null) {
                productName = product.getComponent(ReelComponent.class).getName();
            } else if (product.getComponent(HookComponent.class) != null) {
                productName = product.getComponent(HookComponent.class).getName();
            }

            float smallerPadding = Configuration.getInstance().getSmallPadding() / 2;

            productPrice = product.getComponent(UpgradeComponent.class).getPrice();

            Label itemLabel = new Label(productName + " (" + productPrice + ")", skin);
            itemLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
            Table itemRow = new Table();
            itemRow.add(itemLabel).left().expandX().padRight(smallerPadding);

            boolean canAfford = gameContext.getWorld().getScore() >= productPrice;
            boolean isPurchased = false;
            if (product.getComponent(UpgradeComponent.class) != null) {
                isPurchased = product.getComponent(UpgradeComponent.class).isUpgraded();
            }

            if (isPurchased) {
                Label purchasedLabel = new Label("Purchased", skin);
                purchasedLabel.setColor(Assets.POSITIVE_GREEN);
                purchasedLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
                itemRow.add(purchasedLabel).padLeft(smallerPadding);
            } else if (canAfford) {
                TextButton buyButton = ButtonFactory.createTextButton("BUY",
                        ButtonFactory.ButtonSize.SMALL, () -> {
                            TransactionEvent txEvent = new TransactionEvent(player.getPlayerEntity(), product);
                            GameEventBus.getInstance().post(txEvent);
                        });
                buyButton.setColor(Assets.POSITIVE_GREEN);
                buyButton.getStyle().fontColor = Color.WHITE;
                itemRow.add(buyButton).size(buyButton.getWidth() + Configuration.getInstance().getMediumPadding(),
                        Configuration.getInstance().getMediumPadding()).padLeft(smallerPadding);
            } else {
                Label notEnoughMoneyLabel = new Label("Not enough money", skin);
                notEnoughMoneyLabel.setColor(Assets.NEGATIVE_RED);
                itemRow.add(notEnoughMoneyLabel).padLeft(smallerPadding);
                notEnoughMoneyLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
            }

            productTable.add(itemRow)
                    .expandX()
                    .fillX()
                    .top()
                    .padBottom(Configuration.getInstance().getSmallPadding())
                    .padLeft(smallerPadding)
                    .padRight(smallerPadding)
                    .row();
        }

        TextButton continueButton = ButtonFactory.createTextButton("Continue",
                ButtonFactory.ButtonSize.MEDIUM, () -> {
                    upgradeStore.setRenderTrader(false);
                    ScreenManager.getInstance().startNextLevel();
                });

        rootTable.add(continueButton)
                .expandX()
                .bottom()
                .padTop(Configuration.getInstance().getLargePadding())
                .padBottom(Configuration.getInstance().getLargePadding())
                .width(continueButton.getWidth())
                .height(continueButton.getHeight());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        super.drawBackground();
        stage.act(delta);
        stage.draw();
    }
}
