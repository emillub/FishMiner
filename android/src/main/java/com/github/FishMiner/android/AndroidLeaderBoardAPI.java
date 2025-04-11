package com.github.FishMiner.android;

import com.github.FishMiner.data.ports.out.ILeaderBoardService;
import com.github.FishMiner.domain.ports.out.LeaderboardCallback;
import com.github.FishMiner.data.Score;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidLeaderBoardAPI implements ILeaderBoardService {
    private final FirebaseFirestore db;

    public AndroidLeaderBoardAPI() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void getTopScores(LeaderboardCallback callback) {
        db.collection("LeaderBoard")
            .orderBy("score", Query.Direction.DESCENDING)
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
    public void submitScore(String username, int score, LeaderboardCallback callback) {
        db.collection("LeaderBoard")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Long existingScore = document.getLong("score");
                        if (existingScore != null && score > existingScore) {
                            db.collection("LeaderBoard").document(document.getId())
                                .update("score", score)
                                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                        } else {
                            callback.onSuccess(null);
                        }
                    }
                } else {
                    Map<String, Object> scoreEntry = new HashMap<>();
                    scoreEntry.put("username", username);
                    scoreEntry.put("score", score);

                    db.collection("LeaderBoard")
                        .add(scoreEntry)
                        .addOnSuccessListener(documentReference -> callback.onSuccess(null))
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                }
            })
            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
