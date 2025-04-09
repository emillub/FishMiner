package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.ScoreComponent;
import com.github.FishMiner.domain.ecs.components.SharkComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;
import com.github.FishMiner.domain.eventBus.GameEventBus;
import com.github.FishMiner.domain.events.impl.ScoreEvent;
import com.github.FishMiner.domain.events.impl.FishCapturedEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;

public class ScoreSystem extends EntitySystem implements IGameEventListener<FishCapturedEvent> {
    private final static String TAG = "ScoreSystem";
    private static final float FLY_DURATION = 1.0f;
    private static final float ARC_HEIGHT = 50f;

    private final ComponentMapper<ScoreComponent> scoreCompMapper = ComponentMapper.getFor(ScoreComponent.class);
    private final ComponentMapper<TransformComponent> transformCompMapper = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<FishComponent> fishCompMapper = ComponentMapper.getFor(FishComponent.class);
    private final ComponentMapper<SharkComponent> sharkCompMapper = ComponentMapper.getFor(SharkComponent.class);


    private final Array<FishScoreData> capturedQueue = new Array<>();
    private static class FishScoreData {
        Entity player;
        Entity fishableObject;
        float flyTime;
        boolean scoreSent;
    }

    public ScoreSystem() {
        Family.all(FishComponent.class, StateComponent.class).get();
        super.setProcessing(false);
    }

    @Override
    public void onEvent(FishCapturedEvent event) {
        setProcessing(true);
        if (event.isHandled()) return;

        Entity scoringPlayer = event.getSource();
        Entity capturedFish = event.getTarget();

        try {
            ValidateUtil.validateMultipleNotNull(scoringPlayer, capturedFish);
            capturedQueue.add(new FishScoreData() {{
                this.player = scoringPlayer;
                this.fishableObject = capturedFish;
                this.flyTime = 0f;
                this.scoreSent = false;
            }});
            event.setHandled();
        } catch (IllegalArgumentException e) {
            Logger.getInstance().error(TAG, "Invalid capture: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(float deltaTime) {
        for (int i = 0; i < capturedQueue.size; i++) {
            FishScoreData entry = capturedQueue.get(i);
            Entity fishedEntity = entry.fishableObject;

            TransformComponent fishPos = transformCompMapper.get(fishedEntity);
            ValidateUtil.validateNotNull(fishPos, "ScoreSystem/update: fishPos was null.");

            // Send fish flying into the boat
            Entity player = entry.player;

            TransformComponent playerPos = transformCompMapper.get(player);
            Vector3 target = new Vector3(
                playerPos.pos.x,
                playerPos.pos.y - 50,
                playerPos.pos.z - 1);

            Vector3 start = new Vector3(fishPos.pos);

            float t = entry.flyTime / FLY_DURATION;
            t = Math.min(t, 1f);

            float x = start.x + t * (target.x - start.x);
            float y = start.y + t * (target.y - start.y) + ARC_HEIGHT * (1 - t) * t; // the arc movment is calcd here

            fishPos.pos.set(x, y, target.z - 1);

            entry.flyTime += deltaTime;
            // At end of animation, award points and dispose fish
            if (entry.flyTime >= FLY_DURATION && !entry.scoreSent) {
                if (!fishedEntity.isScheduledForRemoval()) {
                    try {
                        FishComponent fishComp = fishCompMapper.get(fishedEntity);
                        ScoreComponent scoreComp = scoreCompMapper.get(player);
                        ValidateUtil.validateMultipleNotNull(
                            fishComp, "fishComp cannot be null",
                            scoreComp, "scoreComp Cannot be null."
                        );

                        SharkComponent sharkComp = sharkCompMapper.get(fishedEntity);
                        float scoreDifference = (sharkComp == null) ? fishComp.getValue() : fishComp.getValue() * sharkComp.getDamage();
                        scoreComp.setScore(scoreDifference);

                        GameEventBus.getInstance().post(new ScoreEvent(scoreComp.getScore()));
                        entry.scoreSent = true;
                        getEngine().removeEntity(fishedEntity);
                    } catch (IllegalArgumentException e) {
                        Logger.getInstance().error(TAG, e.getMessage(), e);
                    }
                }
                capturedQueue.removeIndex(i--); // Maintain index after removal
            }
        }
        if (capturedQueue.size <= 0) {
            setProcessing(false);
        }
    }

    @Override
    public Class<FishCapturedEvent> getEventType() {
        return FishCapturedEvent.class;
    }
}
