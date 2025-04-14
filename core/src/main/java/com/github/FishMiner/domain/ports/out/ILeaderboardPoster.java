package com.github.FishMiner.domain.ports.out;

import com.github.FishMiner.domain.ports.in.data.LeaderboardCallback;
import com.github.FishMiner.data.ScoreEntry;

public interface ILeaderboardPoster {
    void postScore(ScoreEntry entry, LeaderboardCallback callback);
}
