package com.github.FishMiner.data.ports.out;

public interface LeaderBoardInterface {
    void submitHighScore(String user, int score);
    void getHighscores(ArrayList<Score> dataHolder);

}
