package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.utils.ZComparator;


public class RenderingSystem extends SortedIteratingSystem {
    private static final String TAG = "RenderingSystem";
    private final SpriteBatch batch;
    private final ComponentMapper<TransformComponent> transMapper = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<AnimationComponent> animMapper = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<TextureComponent> texMapper = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<RotationComponent> rotMapper = ComponentMapper.getFor(RotationComponent.class);
    private final ComponentMapper<VelocityComponent> velocityMapper = ComponentMapper.getFor(VelocityComponent.class);
    protected ImmutableArray<Entity> entities;
    private OrthographicCamera cam;

    public RenderingSystem(SpriteBatch batch, OrthographicCamera cam) {
        super(
            Family.all(TransformComponent.class, TextureComponent.class).get(),
            new ZComparator()
        );
        this.batch = batch;
        this.cam = cam;
        entities = getEntities();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        entities = getEntities();
        cam.update();

        batch.setProjectionMatrix(cam.combined);
        batch.enableBlending();

        batch.begin();

        for (Entity entity : getEntities()) {
            TransformComponent pos = transMapper.get(entity);
            AnimationComponent anim = animMapper.get(entity);
            TextureComponent tex = texMapper.get(entity);
            VelocityComponent vel = velocityMapper.get(entity);
            RotationComponent rot = rotMapper.get(entity);
            try {
                // These can never be null
                ValidateUtil.validateMultipleNotNull(pos, tex);
            } catch (IllegalArgumentException e) {
                Logger.getInstance().debug(TAG, "Some entity was missing transform or texture component", e);
                continue;
            }

            float rotation = (rot != null) ? rot.angle : pos.rotation;
            float scaleX = (pos != null) ? pos.scale.x : 1f;
            float scaleY = (pos != null) ? pos.scale.y : 1f;

            if (vel != null) {
                if (vel.velocity.x > 0) {
                    scaleX = -scaleX;
                }
            }
            TextureRegion frame = (anim != null) ? anim.currentAnimation.getKeyFrame(anim.timer, true) : tex.getRegion();

            //TextureRegion frame = anim.currentAnimation.getKeyFrame(anim.timer, true);

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
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public OrthographicCamera getCam() {
        return cam;
    }
}
