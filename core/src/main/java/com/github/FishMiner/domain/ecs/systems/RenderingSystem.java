package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;


public class RenderingSystem extends IteratingSystem {
    private SpriteBatch batch;
    private ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);
    private ComponentMapper<RotationComponent> rm = ComponentMapper.getFor(RotationComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final float scale = Configuration.getInstance().getUniformScale();


    public RenderingSystem(SpriteBatch batch) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get());
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
        VelocityComponent vel = vm.get(entity);
        RotationComponent rot = rm.get(entity);

        float rotation = (rot != null) ? rot.angle : 0f;
        float scaleX = (pos != null) ? pos.scale.x * scale : scale;
        float scaleY = (pos != null) ? pos.scale.y * scale : scale;

        if (vel != null) {
            if (vel.velocity.x > 0) {
                scaleX = -scaleX;
            }
        }


        if (pos != null && anim != null && anim.currentAnimation != null) {
            TextureRegion frame = anim.currentAnimation.getKeyFrame(anim.timer, true);

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
        } else if (pos != null && tex != null && anim == null) {
            float originX = tex.getFrameWidth() * 0.5f;
            float originY = tex.getFrameHeight() * 0.5f;

            batch.draw(tex.getRegion(),
                pos.pos.x - originX,
                pos.pos.y - originY,
                originX,
                originY,
                tex.getRegion().getRegionWidth(),
                tex.getRegion().getRegionHeight(),
                scaleX,
                scaleY,
                rotation
            );
        }
    }
}
