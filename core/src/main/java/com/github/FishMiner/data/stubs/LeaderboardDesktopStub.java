package com.github.FishMiner.data.stubs;

import com.github.FishMiner.data.ports.out.LeaderboardInterface;

public class LeaderboardDesktopStub implements LeaderboardInterface {
    @Override
    public void submitScore(String username, int score) {
        System.out.println("Desktop stub: submitScore(" + username + ", " + score + ")");
    }

    @Override
    public void getTopScores() {
        System.out.println("Desktop stub: getTopScores()");
    }
}
