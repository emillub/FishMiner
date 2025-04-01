package com.github.FishMiner.domain.ecs.systems.test;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
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


        private final ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
        private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
        private final ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);
        private final ComponentMapper<RotationComponent> rm = ComponentMapper.getFor(RotationComponent.class);
        private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
        private final ComponentMapper<BoundsComponent> bm = ComponentMapper.getFor(BoundsComponent.class);

        /**
         * This system will process all entities with a PositionComponent.
         */
        public DebugRenderingSystem() {
            super(Family.all(TransformComponent.class).get());
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
            //AnimationComponent anim = am.get(entity);
            //RotationComponent rot = rm.get(entity);
            //VelocityComponent vel = vm.get(entity);
            enableBoundsRenderer(true, entity);
            enableTextureBoxes(false, entity);



            }
            /**
             * if bound boxes is switched on entities without bounds will have a blue square
             * at their center
             */
            private void enableBoundsRenderer(boolean enable, Entity entity) {
                if (enable) {
                    BoundsComponent bound = bm.get(entity);
                    TransformComponent pos = pm.get(entity);

                    boundsRenderer.setColor(Color.BLUE);

                    float boundWidth = 5;
                    float boundHeight = 5;
                    float boundPosX = pos.pos.x;
                    float boundPosY = pos.pos.y;

                    if (bound != null) {
                        boundWidth = bound.bounds.getWidth();
                        boundHeight = bound.bounds.getHeight();
                        boundPosX = bound.bounds.x;
                        boundPosY = bound.bounds.y;
                    }
                    boundsRenderer.rect(boundPosX, boundPosY, boundWidth, boundHeight);
                }
            }

            private void enableTextureBoxes(boolean enable, Entity entity) {
                if (enable) {
                    TransformComponent pos = pm.get(entity);
                    TextureComponent tex = tm.get(entity);
                    texRenderer.setColor(Color.YELLOW);
                    posRenderer.setColor(Color.RED);

                    // Render a square at the entity's position.
                    // Here we use a fixed size (e.g., 32x32) and center the square on the entity's position.
                    float texWidth = 1;
                    float texHeight = 1;

                    if (tex != null) {
                        texWidth = tex.getFrameWidth();
                        texHeight = tex.getFrameHeight();
                    }
                    // TODO: rotate tex
                    texRenderer.rect(pos.pos.x - texWidth / 2, pos.pos.y - texHeight / 2, texWidth, texHeight);
                    posRenderer.point(pos.pos.x, pos.pos.y, 10);
            }
        }
}
