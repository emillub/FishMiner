package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.ReelComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.utils.ZComparator;

public class RenderingSystem extends SortedIteratingSystem {
    private final SpriteBatch batch;
    private final OrthographicCamera cam;

    private final ComponentMapper<TransformComponent> transMapper = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<AnimationComponent> animMapper = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<TextureComponent> texMapper = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<RotationComponent> rotMapper = ComponentMapper.getFor(RotationComponent.class);
    private final ComponentMapper<VelocityComponent> velocityMapper = ComponentMapper.getFor(VelocityComponent.class);

    public RenderingSystem(SpriteBatch batch, OrthographicCamera cam) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());
        this.batch = batch;
        this.cam = cam;
    }

    @Override
    public void update(float deltaTime) {
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.enableBlending();
        batch.begin();

        for (Entity entity : getEntities()) {
            TransformComponent pos = transMapper.get(entity);
            if (pos == null || pos.pos == null)
                continue;
            if (cam.frustum.boundsInFrustum(pos.pos.x, pos.pos.y, pos.pos.z, 1, 1, 1)) {
                drawEntity(entity, deltaTime);
            }
        }

        batch.end();
    }

    private void drawEntity(Entity entity, float deltaTime) {
        TransformComponent pos = transMapper.get(entity);
        TextureComponent tex = texMapper.get(entity);
        AnimationComponent anim = animMapper.get(entity);
        VelocityComponent vel = velocityMapper.get(entity);
        RotationComponent rot = rotMapper.get(entity);

        if (pos == null || tex == null) return;

        TextureRegion frame = (anim != null)
            ? anim.currentAnimation.getKeyFrame(anim.timer, true)
            : tex.getRegion();

        if (frame == null) return;

        float rotation = (rot != null) ? rot.angle : pos.rotation;
        float scaleX = (vel != null && vel.velocity.x > 0) ? -pos.scale.x : pos.scale.x;
        float scaleY = pos.scale.y;
        float originX = frame.getRegionWidth() / 2f;
        float originY = frame.getRegionHeight() / 2f;

        batch.draw(
            frame,
            pos.pos.x - originX,
            pos.pos.y - originY,
            originX,
            originY,
            frame.getRegionWidth(),
            frame.getRegionHeight(),
            scaleX,
            scaleY,
            rotation
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // This method is required by SortedIteratingSystem but we already render in update()
    }

    public OrthographicCamera getCam() {
        return cam;
    }
}
