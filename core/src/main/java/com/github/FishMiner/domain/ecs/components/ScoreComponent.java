package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;

public class ScoreComponent implements Component {
    private float score = 0;

    public void setScore(float score) {
        this.score = score;
    }

    public void addScore(float scoreDelta) {
        this.score = Math.max(0, this.score + scoreDelta);
    }


    public float getScore() {
        return score;
    }
}
