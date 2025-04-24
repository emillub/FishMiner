package com.github.FishMiner.domain.ports.out.data.callbacks;

import com.github.FishMiner.data.ScoreEntry;

import java.util.List;

public interface LeaderboardCallback {
    void onSuccess(List<ScoreEntry> topScoreEntries);
    void onFailure(String errorMessage);
}
