package com.github.FishMiner.data.ports.out;

import com.github.FishMiner.domain.ports.in.data.LeaderboardCallback;

public  interface ILeaderBoardService {
    void getTopScores(LeaderboardCallback callback);
    void submitScore(String username, int score, LeaderboardCallback callback);
    void getUserScore(String username, LeaderboardCallback callback);

}
