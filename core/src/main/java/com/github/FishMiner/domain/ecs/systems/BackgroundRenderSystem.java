package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.FishMiner.infrastructure.Configuration;

public class BackgroundRenderSystem extends EntitySystem {
    private final ShapeRenderer shapeRenderer;

    public BackgroundRenderSystem(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    @Override
    public void update(float deltaTime) {
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
    }
}

