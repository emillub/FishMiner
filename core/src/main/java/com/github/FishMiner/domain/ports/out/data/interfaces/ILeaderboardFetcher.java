package com.github.FishMiner.domain.ports.out.data.interfaces;

import com.github.FishMiner.domain.ports.out.data.callbacks.LeaderboardCallback;

public interface ILeaderboardFetcher {
    void fetchLeaderboard(LeaderboardCallback callback);
}
