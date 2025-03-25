package com.github.FishMiner.domain.ecs.systems.test;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;

/**
 * Test and debug system to show the actual size of the entities
 */
public class DebugRenderingSystem extends IteratingSystem {
        private final ShapeRenderer texRenderer;
        private final ShapeRenderer boundsRenderer;
        private final ShapeRenderer posRenderer;


        private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
        private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
        private final ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);
        private final ComponentMapper<RotationComponent> rm = ComponentMapper.getFor(RotationComponent.class);
        private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
        private final ComponentMapper<BoundsComponent> bm = ComponentMapper.getFor(BoundsComponent.class);

        /**
         * This system will process all entities with a PositionComponent.
         */
        public DebugRenderingSystem() {
            super(Family.all(PositionComponent.class).get());
            texRenderer = new ShapeRenderer();
            boundsRenderer = new ShapeRenderer();
            posRenderer = new ShapeRenderer();
        }

        @Override
        public void update(float deltaTime) {
            // Begin ShapeRenderer in Filled mode to render solid squares.
            texRenderer.begin(ShapeRenderer.ShapeType.Filled);
            boundsRenderer.begin(ShapeRenderer.ShapeType.Filled);
            posRenderer.begin(ShapeRenderer.ShapeType.Point);
            super.update(deltaTime);
            texRenderer.end();
            boundsRenderer.end();
            posRenderer.end();
        }

        @Override
        protected void processEntity(Entity entity, float deltaTime) {
            PositionComponent pos = pm.get(entity);
            AnimationComponent anim = am.get(entity);
            TextureComponent tex = tm.get(entity);
            RotationComponent rot = rm.get(entity);
            VelocityComponent vel = vm.get(entity);
            BoundsComponent bound = bm.get(entity);

            // Choose a color based on the presence of additional components.
            // You can change this logic to suit your debugging needs.
            Color color = Color.WHITE; // default color
            if (anim != null) {
                color = Color.BLUE;
            } else if (tex != null) {
                color = Color.GREEN;
            } else if (vel != null) {
                color = Color.RED;
            } else if (bound != null) {
                color = Color.YELLOW;
            }

            texRenderer.setColor(color);
            boundsRenderer.setColor(color);
            posRenderer.setColor(Color.CLEAR_WHITE);

            // Render a square at the entity's position.
            // Here we use a fixed size (e.g., 32x32) and center the square on the entity's position.
            float texWidth = 1;
            float texHeight = 1;

            if (tex != null) {
                texWidth = tex.getFrameWidth();
                texHeight = tex.getFrameHeight();
            }

            float boundWidth = 1;
            float boundHeight = 1;
            float boundPosX = pos.position.x;
            float boundPosY = pos.position.y;
            if (bound != null) {
                boundWidth = bound.bounds.getWidth();
                boundHeight = bound.bounds.getHeight();
                boundPosX = bound.bounds.x;
                boundPosY = bound.bounds.y;
                boundsRenderer.rect(boundPosX - boundWidth / 2, boundPosY - boundHeight / 2, boundWidth, boundHeight);
            }

            // TODO: rotate tex
            texRenderer.rect(pos.position.x - texWidth / 2, pos.position.y - texHeight / 2, texWidth, texHeight);
            posRenderer.point(pos.position.x, pos.position.y, 10);
        }
}
