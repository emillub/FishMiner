package com.github.FishMiner.data;

import com.github.FishMiner.common.ValidateUtil;

public record ScoreEntry(String username, int score) {
    public ScoreEntry {
        ValidateUtil.validateNotNull(username, "username");
        ValidateUtil.validateMoreThanZero(username.length());
        ValidateUtil.validatePositiveInt(score, "score");
    }
}
