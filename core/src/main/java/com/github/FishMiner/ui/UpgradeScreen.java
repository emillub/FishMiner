package com.github.FishMiner.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.github.FishMiner.domain.shop.UpgradeItem;
import com.github.FishMiner.domain.shop.UpgradeStore;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.ui.controller.ScreenManager;
import java.util.Map;

public class UpgradeScreen extends AbstractScreen{

    private final InventoryComponent inventory;
    private final Map<String, UpgradeItem> shopItems;
    private final int nextLevel;
    //private final float previousScore;

    public UpgradeScreen(int nextLevel, InventoryComponent inventory){
        super();
        //testing
        if (inventory == null) {
            throw new IllegalArgumentException("UpgradeScreen received a null InventoryComponent");
        }
        this.nextLevel = nextLevel;
        //this.previousScore = previousScore;
        this.inventory = inventory;
        this.shopItems = UpgradeStore.getAvailableUpgrades();


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); // ← essential for buttons to respond

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(40);
        table.setBackground(skin.newDrawable("white", 0.6f, 0.85f, 1f, 0.8f));
        stage.addActor(table);

        Label title = new Label("Upgrade Store", skin);
        title.setFontScale(2f);
        table.add(title).padBottom(30).row();

        final Label moneyLabel = new Label("Points: " + inventory.money, skin);
        moneyLabel.setFontScale(1.3f);
        table.add(moneyLabel).padBottom(30).row();

        for (UpgradeItem item : shopItems.values()) {
            Label itemLabel = new Label(item.name + " (" + item.cost + ")", skin);
            itemLabel.setFontScale(1.1f);

            final TextButton buyButton = new TextButton("Buy", skin);
            buyButton.pad(6);

            if (inventory.hasUpgrade(item.id)) {
                buyButton.setDisabled(true);
                buyButton.setText(item.name + " (Owned)");
            }

            buyButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!inventory.hasUpgrade(item.id) && inventory.money >= item.cost) {
                        inventory.spendMoney(item.cost);
                        inventory.addUpgrade(item.id);
                        buyButton.setDisabled(true);
                        buyButton.setText(item.name + " (Owned)");
                        moneyLabel.setText("Points: " + inventory.money);
                    }
                }
            });

            Table itemRow = new Table();
            itemRow.add(itemLabel).left().expandX().padRight(15);
            itemRow.add(buyButton).right();

            table.add(itemRow).padBottom(20).fillX().width(400).row();
        }

        TextButton continueButton = new TextButton("Continue", skin);
        continueButton.getLabel().setFontScale(1.3f);

        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("🎮 Continue to level " + nextLevel);
                ScreenManager.getInstance().startNextLevel(nextLevel, inventory.money, inventory);
            }
        });

        table.add(continueButton).padTop(40).width(240).height(70).colspan(2).center().row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
}
