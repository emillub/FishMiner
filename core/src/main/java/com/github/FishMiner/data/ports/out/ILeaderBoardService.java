package com.github.FishMiner.data.ports.out;

import com.github.FishMiner.domain.ports.out.LeaderboardCallback;

public  interface ILeaderBoardService {
    void getTopScores(LeaderboardCallback callback);
    void submitScore(String username, int score, LeaderboardCallback callback);
}
