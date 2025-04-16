package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.github.FishMiner.common.ValidateUtil;

public class ScoreComponent implements Component {
    private float score = 0;
    public void addScore(float scoreDiff) {
        score = Math.max(0, score + scoreDiff);
    }

    public void setScore(float newScore) {
        ValidateUtil.validatePositiveFloat(newScore, "newScore");
        this.score = newScore;
    }

    public float getScore() {
        return score;
    }
}
