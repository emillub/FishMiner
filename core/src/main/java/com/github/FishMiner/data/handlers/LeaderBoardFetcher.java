package com.github.FishMiner.data.handlers;

import com.github.FishMiner.data.services.ILeaderBoardService;

public class LeaderBoardFetcher implements ILeaderBoardService {
    private final ILeaderBoardService leaderBoardService;

    public LeaderBoardFetcher(ILeaderBoardService leaderBoardService){
        this.leaderBoardService = leaderBoardService;
    }
    @Override
    public void getTopScores(){
        leaderBoardService.getTopScores();
    }
}
