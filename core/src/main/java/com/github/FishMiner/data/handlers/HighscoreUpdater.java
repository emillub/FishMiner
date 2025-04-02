package com.github.FishMiner.data.handlers;

import com.github.FishMiner.data.services.IHighscoreService;


public class HighscoreUpdater implements IHighscoreService {
    private final IHighscoreService highscoreService;

    public HighscoreUpdater(IHighscoreService highscoreService) {
        this.highscoreService = highscoreService;
    }

    @Override
    public void submitScore(String username, int score) {
        highscoreService.submitScore(username, score);
    }
}
