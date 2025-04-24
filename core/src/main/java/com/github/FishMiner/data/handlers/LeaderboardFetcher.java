package com.github.FishMiner.data.handlers;

import com.github.FishMiner.data.ScoreEntry;
import com.github.FishMiner.data.ports.out.ILeaderBoardService;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.out.data.interfaces.ILeaderboardFetcher;
import com.github.FishMiner.domain.ports.out.data.callbacks.LeaderboardCallback;

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

                if (currentUser != null) {
                    for (ScoreEntry entry : topScoreEntries) {
                        if (entry.username().equals(currentUser)) {
                            userInTop = true;
                            break;
                        }
                    }

                    if (!userInTop) {
                        // 🔁 Hent brukerens score etter en liten delay
                        new Thread(() -> {
                            try {
                                Thread.sleep(300); // ⏳ Vent 300ms på Firestore sync
                            } catch (InterruptedException ignored) {}

                            leaderboardService.getUserScore(currentUser, new LeaderboardCallback() {
                                @Override
                                public void onSuccess(List<ScoreEntry> userScore) {
                                    if (!userScore.isEmpty()) {
                                        topScoreEntries.add(null); // bruk null som separator
                                        topScoreEntries.addAll(userScore);
                                    }
                                    callback.onSuccess(topScoreEntries);
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    callback.onSuccess(topScoreEntries); // fallback: vis topp 10
                                }
                            });
                        }).start();
                    } else {
                        callback.onSuccess(topScoreEntries); // bruker er allerede i topp 10
                    }

                } else {
                    callback.onSuccess(topScoreEntries); // ikke logget inn
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }

}
