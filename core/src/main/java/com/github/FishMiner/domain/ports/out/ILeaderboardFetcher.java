package com.github.FishMiner.domain.ports.out;

import com.github.FishMiner.domain.ports.in.data.LeaderboardCallback;

public interface ILeaderboardFetcher {
    void fetchLeaderboard(LeaderboardCallback callback);
}
