package com.github.FishMiner.ui.interfaces;

import com.github.FishMiner.data.services.LeaderboardCallback;

public interface IRequestManger {
    void submitScore(String username, int score);
    void getTopScores(LeaderboardCallback callback);
}
