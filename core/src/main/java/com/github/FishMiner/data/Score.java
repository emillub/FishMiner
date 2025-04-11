package com.github.FishMiner.data;

import com.github.FishMiner.common.ValidateUtil;

public record Score(String username, int score) {
    public Score {
        ValidateUtil.validateNotNull(username, "username");
        ValidateUtil.validateMoreThanZero(username.length());
        ValidateUtil.validatePositiveInt(score, "score");
    }
}
