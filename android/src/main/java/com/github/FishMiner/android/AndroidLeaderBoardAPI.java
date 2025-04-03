package com.github.FishMiner.android;

import com.github.FishMiner.data.services.ILeaderBoardService;
import com.github.FishMiner.data.services.LeaderboardCallback;
import com.github.FishMiner.data.Score;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AndroidLeaderBoardAPI implements ILeaderBoardService {
    private final FirebaseFirestore db;

    public AndroidLeaderBoardAPI() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void getTopScores(LeaderboardCallback callback) {
        db.collection("LeaderBoard")
            .orderBy("score", Query.Direction.DESCENDING) // Sort by highest score
            .limit(10) // Get top 10 scores
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Score> scores = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String username = document.getString("username");
                        Long scoreValue = document.getLong("score");
                        if (username != null && scoreValue != null) {
                            scores.add(new Score(username, scoreValue.intValue()));
                        }
                    }
                    callback.onSuccess(scores);
                } else {
                    callback.onFailure(task.getException().getMessage());
                }
            });
    }
}
