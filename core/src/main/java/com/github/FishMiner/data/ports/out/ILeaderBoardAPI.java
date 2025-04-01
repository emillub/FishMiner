package com.github.FishMiner.data.ports.out;

public  interface ILeaderBoardAPI {
    void submitScore(String username, int score);
    void getTopScores();
}
