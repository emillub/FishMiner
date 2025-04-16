package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.ui.ports.out.ScreenType;

public class BackgroundRenderSystem extends EntitySystem {
    private static final String TAG = "BackgroundRenderSystem";
    private final ShapeRenderer shapeRenderer;
    private ScreenType currentScreen;
    private ChangeScreenEvent currentScreenEvent = null;

    public BackgroundRenderSystem(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
        GameEventBus.getInstance().register(this.getChangeScreenListener());
    }

    //@Override
    //public void addedToEngine (Engine engine) {
    //}

    @Override
    public void update(float deltaTime) {
        if (currentScreenEvent == null) {
            return;
        }
        if (currentScreenEvent.getScreenType().equals(ScreenType.PLAY)) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            // Light blue ocean surface
            shapeRenderer.setColor(0.5f, 0.8f, 1f, 1f);
            shapeRenderer.rect(
                0,
                Configuration.getInstance().getOceanHeight(),
                Configuration.getInstance().getScreenWidth(),
                Configuration.getInstance().getScreenHeight() - Configuration.getInstance().getOceanHeight()
            );

            // Depth shading
            int levels = Configuration.getInstance().getDepthLevels();
            int levelHeight = Configuration.getInstance().getOceanHeight() / levels;
            for (int i = 0; i < levels; i++) {
                float shade = 0.2f + (i * 0.15f);
                shapeRenderer.setColor(0f, 0f, shade, 1f);
                shapeRenderer.rect(
                    0,
                    i * levelHeight,
                    Configuration.getInstance().getScreenWidth(),
                    levelHeight
                );
            }

            shapeRenderer.end();
        } else if (currentScreen == ScreenType.UPGRADE) {

        }
    }

    /**
     * Returns an event listener for Screen Changes
     */
    public IGameEventListener<ChangeScreenEvent> getChangeScreenListener() {
        return new IGameEventListener<ChangeScreenEvent>() {
            @Override
            public void onEvent(ChangeScreenEvent event) {
                if (event.getScreenType() == ScreenType.PLAY || event.getScreenType() == ScreenType.UPGRADE) {
                    currentScreenEvent = event;
                }
            }

            @Override
            public Class<ChangeScreenEvent> getEventType() {
                return ChangeScreenEvent.class;
            }
        };
    }
}

