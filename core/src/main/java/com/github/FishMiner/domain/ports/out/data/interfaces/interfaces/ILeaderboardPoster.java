package com.github.FishMiner.domain.ports.out.data.interfaces.interfaces;

import com.github.FishMiner.domain.ports.out.data.callbacks.callbacks.LeaderboardCallback;
import com.github.FishMiner.data.ScoreEntry;

public interface ILeaderboardPoster {
    void postScore(ScoreEntry entry, LeaderboardCallback callback);
}
