package com.github.FishMiner.data.ports.out;

public  interface LeaderboardInterface {
    void submitScore(String username, int score);
    void getTopScores();
}
