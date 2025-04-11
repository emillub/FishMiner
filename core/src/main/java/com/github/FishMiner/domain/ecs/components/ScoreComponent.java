package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;

public class ScoreComponent implements Component {
    private float score = 0;
    public void addScore(float newScore) {
        score = Math.max(0, score + newScore);
    }

    public void setScore(float score) {
        addScore(score);
    }

    public float getScore() {
        return score;
    }
}
