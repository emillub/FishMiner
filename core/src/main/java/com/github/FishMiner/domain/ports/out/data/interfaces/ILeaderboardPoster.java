package com.github.FishMiner.domain.ports.out.data.interfaces;

import com.github.FishMiner.domain.ports.out.data.callbacks.LeaderboardCallback;
import com.github.FishMiner.data.ScoreEntry;

public interface ILeaderboardPoster {
    void postScore(ScoreEntry entry, LeaderboardCallback callback);
}
