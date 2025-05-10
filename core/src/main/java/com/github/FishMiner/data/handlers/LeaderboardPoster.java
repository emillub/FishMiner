package com.github.FishMiner.data.handlers;

import com.github.FishMiner.data.ports.out.ILeaderBoardService;
import com.github.FishMiner.data.ScoreEntry;
import com.github.FishMiner.domain.ports.out.data.interfaces.ILeaderboardPoster;
import com.github.FishMiner.domain.ports.out.data.callbacks.LeaderboardCallback;

public class LeaderboardPoster implements ILeaderboardPoster {

    private final ILeaderBoardService leaderboardService;

    public LeaderboardPoster(ILeaderBoardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @Override
    public void postScore(ScoreEntry entry, LeaderboardCallback callback) {
        leaderboardService.submitScore(entry.username(), entry.score(), callback);
    }
}
