package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;


public class RenderingSystem extends IteratingSystem {
    private SpriteBatch batch;
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<RotationComponent> rm = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);

    public RenderingSystem(SpriteBatch batch) {
        super(Family.all(PositionComponent.class).get());
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
        PositionComponent pos = pm.get(entity);
        AnimationComponent anim = am.get(entity);
        TextureComponent tex = tm.get(entity);
        RotationComponent rot = rm.get(entity);

        float rotation = (rot != null) ? rot.angle : 0f;

        if (pos != null && anim != null && anim.currentAnimation != null) {
            TextureRegion frame = anim.currentAnimation.getKeyFrame(anim.timer, true);
            batch.draw(frame,
                pos.position.x, pos.position.y,
                frame.getRegionWidth() / 2f, frame.getRegionHeight() / 2f,
                frame.getRegionWidth(), frame.getRegionHeight(),
                1f, 1f,
                rotation
            );
        } else if (tex != null) {
            batch.draw(tex.getRegion(),
                pos.position.x, pos.position.y,
                tex.getRegion().getRegionWidth() / 2f, tex.getRegion().getRegionHeight() / 2f,  // Origin
                tex.getRegion().getRegionWidth(), tex.getRegion().getRegionHeight(),
                1f, 1f,
                rotation
            );
        }
    }
}
