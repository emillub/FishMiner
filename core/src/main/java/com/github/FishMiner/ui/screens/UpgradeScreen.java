package com.github.FishMiner.ui.screens;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.ecsEvents.TransactionEvent;
import com.github.FishMiner.domain.ecs.components.SinkerComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.ReelComponent;
import com.github.FishMiner.domain.ecs.components.UpgradeComponent;
import com.github.FishMiner.domain.UpgradeStore;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.ui.factories.ButtonFactory;
import com.github.FishMiner.ui.ports.in.IPlayer;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;

import java.io.ObjectInputFilter.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeScreen extends AbstractScreen implements IGameScreen {
    private static final String TAG = "UpgradeScreen";
    private final List<Entity> upgradeProducts;
    private final IPlayer player;
    private final UpgradeStore upgradeStore;
    private float smallerPadding = Configuration.getInstance().getSmallPadding() / 2;

    private List<Table> itemRows = new ArrayList<>();
    Entity reelProduct = null;
    Entity hookProduct = null;
    Entity sinkerProduct = null;

    private Label scoreLabel;
    private Table productTable;

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
        reelProduct = null;
        hookProduct = null;
        sinkerProduct = null;
        for (Entity product : upgradeProducts) {
            if (product.getComponent(UpgradeComponent.class) == null) {
                Logger.getInstance().error(TAG, "Product " + product + " does not have an UpgradeComponent. Skipping.");
                continue;
            }
            if (product.getComponent(UpgradeComponent.class).isUpgraded()) {
                continue;
            }
            if (product.getComponent(SinkerComponent.class) != null && sinkerProduct == null) {
                sinkerProduct = product;
            } else if (product.getComponent(ReelComponent.class) != null && reelProduct == null) {
                reelProduct = product;
            } else if (product.getComponent(HookComponent.class) != null && hookProduct == null) {
                hookProduct = product;
            }
        }

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Label titleLabel = new Label("Upgrade Store", skin);
        titleLabel.setFontScale(Configuration.getInstance().getLargeFontScale());
        rootTable.add(titleLabel).expandX().top().padTop(Configuration.getInstance().getLargePadding())
                .padBottom(Configuration.getInstance().getLargePadding()).row();

        scoreLabel = new Label("Current Score: " + player.getScore(), skin);
        scoreLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
        rootTable.add(scoreLabel).expandX().top().padTop(Configuration.getInstance().getSmallPadding())
                .padBottom(Configuration.getInstance().getSmallPadding()).row();

        productTable = new Table();

        ScrollPane scrollPane = new ScrollPane(productTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        rootTable.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.2f))); // Transparent white background

        rootTable.add(scrollPane)
                .expandY()
                .fillY()
                .row();

        updateProductTable();

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
                .height(continueButton.getHeight()).row();
    }

    private void addProductToTable(Table productTable, Entity product, String productType) {
        String productName = productType;
        Table itemRow = new Table();
        addRowContent(itemRow, product, productName);

        productTable.add(itemRow)
                .expandX()
                .fillX()
                .top()
                .padTop(smallerPadding)
                .padLeft(Configuration.getInstance().getSmallPadding())
                .padRight(Configuration.getInstance().getSmallPadding())
                .row();
    }

    private void updateScoreLabel() {
        if (scoreLabel != null) {
            scoreLabel.setText("Current Score: " + (int) player.getScore());
        }
    }

    private void addRowContent(Table itemRow, Entity product, String productName) {
        int productPrice = product.getComponent(UpgradeComponent.class).getPrice();
        boolean canAfford = player.getScore() >= productPrice;

        Label itemLabel = createItemLabel(productName + " (" + productPrice + ")");
        itemRow.add(itemLabel).left().expandX().pad(smallerPadding);

        if (product.getComponent(UpgradeComponent.class).isUpgraded()) {
            itemRow.add(createPurchasedLabel()).padLeft(smallerPadding);
        } else if (canAfford) {
            itemRow.add(createBuyButton(product, productName, itemRow)).size(
                    Configuration.getInstance().getMediumPadding() * 2,
                    Configuration.getInstance().getMediumPadding()).padLeft(smallerPadding);
        } else {
            itemRow.add(createNotEnoughMoneyLabel()).padLeft(smallerPadding);
        }
    }

    private Label createItemLabel(String productName) {
        Label itemLabel = new Label(productName, skin);
        itemLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
        return itemLabel;
    }

    private Label createPurchasedLabel() {
        Label purchasedLabel = new Label("Purchased", skin);
        purchasedLabel.setColor(Assets.POSITIVE_GREEN);
        purchasedLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
        return purchasedLabel;
    }

    private TextButton createBuyButton(Entity product, String productName, Table itemRow) {
        TextButton buyButton = ButtonFactory.createTextButton("BUY", ButtonFactory.ButtonSize.SMALL, () -> {
            TransactionEvent transactionEvent = new TransactionEvent(player.getPlayerEntity(), product);
            GameEventBus.getInstance().post(transactionEvent);
            updateScoreLabel();
            updateProductTable();
        });
        buyButton.setColor(Assets.POSITIVE_GREEN);
        buyButton.getStyle().fontColor = Color.WHITE;
        return buyButton;
    }

    private Label createNotEnoughMoneyLabel() {
        Label notEnoughMoneyLabel = new Label("Not enough money", skin);
        notEnoughMoneyLabel.setColor(Assets.NEGATIVE_RED);
        notEnoughMoneyLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
        return notEnoughMoneyLabel;
    }

    private void updateProductTable() {
        productTable.clearChildren();
        if (hookProduct == null) {
            hookProduct = player.getHook();
        }
        if (reelProduct == null) {
            reelProduct = player.getReel();
        }
        if (sinkerProduct == null) {
            sinkerProduct = player.getSinker();
        }
        addProductToTable(productTable, hookProduct, hookProduct.getComponent(HookComponent.class).name);
        addProductToTable(productTable, reelProduct, reelProduct.getComponent(ReelComponent.class).name);
        addProductToTable(productTable, sinkerProduct, sinkerProduct.getComponent(SinkerComponent.class).name);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        super.drawBackground();
        stage.act(delta);
        stage.draw();
    }
}
