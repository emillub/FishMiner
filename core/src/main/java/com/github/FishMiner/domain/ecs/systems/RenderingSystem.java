package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;


public class RenderingSystem extends IteratingSystem {
    private SpriteBatch batch;
    private ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<RotationComponent> rm = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);


    public RenderingSystem(SpriteBatch batch) {
        super(Family.all(TransformComponent.class).get());
        this.batch = batch;
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent pos = pm.get(entity);
        AnimationComponent anim = am.get(entity);
        TextureComponent tex = tm.get(entity);
        RotationComponent rot = rm.get(entity);
        VelocityComponent vel = vm.get(entity);


        float rotation = (rot != null) ? rot.angle : 0f;

        float scaleX = 1f;
        if (vel != null && vel.velocity.x > 0) {
            scaleX = -1f;
        }

        if (pos != null && anim != null && anim.currentAnimation != null) {
            TextureRegion frame = anim.currentAnimation.getKeyFrame(anim.timer, true);

            float originX = frame.getRegionWidth() / 2f;
            float originY = frame.getRegionHeight() / 2f;

            batch.draw(
                frame,
                pos.pos.x + originX,
                pos.pos.y + originY,
                originX,
                originY,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                scaleX,
                1f,
                rotation
            );
        } else if (tex != null) {
            batch.draw(tex.getRegion(),
                pos.pos.x, pos.pos.y,
                tex.getRegion().getRegionWidth() / 2f, tex.getRegion().getRegionHeight() / 2f,  // Origin
                tex.getRegion().getRegionWidth(), tex.getRegion().getRegionHeight(),
                1f, 1f,
                rotation
            );
        }
    }
}
