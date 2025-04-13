package com.github.FishMiner.data.handlers;

import com.github.FishMiner.data.ports.out.ILeaderBoardService;
import com.github.FishMiner.domain.ports.out.ILeaderboardFetcher;
import com.github.FishMiner.domain.ports.in.data.LeaderboardCallback;

public class LeaderboardFetcher implements ILeaderboardFetcher {

    private final ILeaderBoardService leaderboardService;

    public LeaderboardFetcher(ILeaderBoardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @Override
    public void fetchLeaderboard(LeaderboardCallback callback) {
        leaderboardService.getTopScores(callback);
    }
}
