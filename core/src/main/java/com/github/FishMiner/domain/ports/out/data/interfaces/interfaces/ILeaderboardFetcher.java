package com.github.FishMiner.domain.ports.out.data.interfaces.interfaces;

import com.github.FishMiner.domain.ports.out.data.callbacks.callbacks.LeaderboardCallback;

public interface ILeaderboardFetcher {
    void fetchLeaderboard(LeaderboardCallback callback);
}
