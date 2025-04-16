package com.github.FishMiner.data.handlers;

import com.github.FishMiner.data.ScoreEntry;
import com.github.FishMiner.data.ports.out.ILeaderBoardService;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.out.ILeaderboardFetcher;
import com.github.FishMiner.domain.ports.in.data.LeaderboardCallback;

import java.util.List;

public class LeaderboardFetcher implements ILeaderboardFetcher {

    private final ILeaderBoardService leaderboardService;

    public LeaderboardFetcher(ILeaderBoardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @Override
    public void fetchLeaderboard(LeaderboardCallback callback) {
        leaderboardService.getTopScores(new LeaderboardCallback() {
            @Override
            public void onSuccess(List<ScoreEntry> topScoreEntries) {
                String currentUser = ScreenManager.getInstance().getGame().getAuthService().getCurrentUsername();
                boolean userInTop = false;

                if (currentUser != null){
                    for (ScoreEntry entry : topScoreEntries){
                        if (entry.username().equals(currentUser)){
                            userInTop = true;
                            break;
                        }
                    }
                    if (!userInTop){
                        leaderboardService.getUserScore(currentUser, new LeaderboardCallback() {
                            @Override
                            public void onSuccess(List<ScoreEntry> userScore) {
                                if (!userScore.isEmpty()) {
                                    topScoreEntries.add(null);
                                    topScoreEntries.addAll(userScore);
                                }
                                callback.onSuccess(topScoreEntries);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                callback.onSuccess(topScoreEntries);
                            }
                        });
                    } else {
                        callback.onSuccess(topScoreEntries);
                    }
                } else {
                    callback.onSuccess(topScoreEntries);
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }
}
