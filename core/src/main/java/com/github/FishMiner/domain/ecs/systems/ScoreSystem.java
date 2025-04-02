package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.ScoreComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;
import com.github.FishMiner.domain.events.GameEventBus;
import com.github.FishMiner.domain.events.ScoreEvent;
import com.github.FishMiner.domain.events.impl.FishCapturedEvent;
import com.github.FishMiner.domain.listeners.IGameEventListener;

public class ScoreSystem extends EntitySystem implements IGameEventListener<FishCapturedEvent> {
    private final static String TAG = "ScoreSystem";
    private static final float FLY_DURATION = 1.0f;

    private static class FishScoreData {
        Entity player;
        Entity fishableObject;
        float flyTime;
        boolean scoreSent;
    }

    FishCapturedEvent event;
    private final Array<FishScoreData> capturedQueue = new Array<>();

    public ScoreSystem() {
        Family.all(FishComponent.class, StateComponent.class).get();
        super.setProcessing(true);
    }

    @Override
    public void onEvent(FishCapturedEvent event) {
        if (event.isHandled()) return;

        Entity scoringPlayer = event.getSource();
        Entity capturedFish = event.getTarget();

        try {
            ValidateUtil.validateNotNull(scoringPlayer, capturedFish);
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
            Entity fish = entry.fishableObject;

            TransformComponent fishPos = fish.getComponent(TransformComponent.class);
            if (fishPos == null) {
                Logger.getInstance().debug(TAG, "fishPos was missing TransformComponent");
            }
            ;

            // Send fish flying into the boat
            Entity player = entry.player;

            TransformComponent playerPos = player.getComponent(TransformComponent.class);
            Vector3 boatPos = new Vector3(playerPos.pos.x, playerPos.pos.y, 2);
            Vector3 start = new Vector3(fishPos.pos);

            float t = entry.flyTime / FLY_DURATION;
            t = Math.min(t, 1f);

            float x = start.x + t * (boatPos.x - start.x);
            float y = start.y + t * (boatPos.y - start.y) + 50 * (1 - t) * t; // the arc movment is calcd here

            entry.flyTime += deltaTime;
            // At end of animation, award points and dispose fish
            if (entry.flyTime >= FLY_DURATION && !entry.scoreSent) {
                FishComponent fishComp = fish.getComponent(FishComponent.class);
                ScoreComponent score = entry.player.getComponent(ScoreComponent.class);

                if (fishComp != null && score != null) {
                    GameEventBus.getInstance().post(new ScoreEvent(fishComp.getValue()));
                    entry.scoreSent = true;
                }

                getEngine().removeEntity(fish);
                capturedQueue.removeIndex(i--); // Maintain index after removal
            }
        }
    }


    @Override
    public Class<FishCapturedEvent> getEventType() {
        return FishCapturedEvent.class;
    }
}
