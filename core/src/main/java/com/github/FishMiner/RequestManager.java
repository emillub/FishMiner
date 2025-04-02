package com.github.FishMiner;

import com.github.FishMiner.domain.handlers.IHighscoreUpdater;
import com.github.FishMiner.domain.handlers.ILeaderBoardFetcher;
import com.github.FishMiner.ui.interfaces.IRequestManger;

public class RequestManager implements IRequestManger {
    private IHighscoreUpdater highscoreUpdater;
    private ILeaderBoardFetcher leaderBoardFetcher;


    private RequestManager(IHighscoreUpdater highscoreUpdater, ILeaderBoardFetcher leaderBoardFetcher) {
        this.highscoreUpdater = highscoreUpdater;
        this.leaderBoardFetcher = leaderBoardFetcher;
    }


    @Override
    public void submitScore(String username, int score) {
        highscoreUpdater.submitScore(username, score);
    }
    @Override
    public void getTopScores(){
        leaderBoardFetcher.getTopScores();
    }
}
