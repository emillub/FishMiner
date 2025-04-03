package com.github.FishMiner.domain.handlers;

import com.github.FishMiner.data.services.LeaderboardCallback;

public interface ILeaderBoardFetcher {
    void getTopScores(LeaderboardCallback callback);
}
