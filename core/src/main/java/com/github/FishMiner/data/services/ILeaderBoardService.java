package com.github.FishMiner.data.services;

import com.github.FishMiner.data.services.LeaderboardCallback;

public  interface ILeaderBoardService {
    void getTopScores(LeaderboardCallback callback);
    void submitScore(String username, int score, LeaderboardCallback callback);
}
