package com.github.FishMiner.domain.ports.out;

import com.github.FishMiner.data.Score;

import java.util.List;

public interface LeaderboardCallback {
    void onSuccess(List<Score> topScores);
    void onFailure(String errorMessage);
}
