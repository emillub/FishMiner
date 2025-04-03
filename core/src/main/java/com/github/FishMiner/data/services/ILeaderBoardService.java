package com.github.FishMiner.data.services;

import com.github.FishMiner.data.services.LeaderboardCallback;

public  interface ILeaderBoardService {
    void getTopScores(LeaderboardCallback callback);
}
