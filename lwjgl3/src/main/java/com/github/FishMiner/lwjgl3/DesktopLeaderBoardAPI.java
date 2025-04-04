package com.github.FishMiner.lwjgl3;

import com.github.FishMiner.data.services.ILeaderBoardService;
import com.github.FishMiner.data.services.LeaderboardCallback;

public class DesktopLeaderBoardAPI implements ILeaderBoardService {
    @Override
    public void getTopScores(LeaderboardCallback callback){}
    @Override
    public void submitScore(String username, int score, LeaderboardCallback callback){}
}
