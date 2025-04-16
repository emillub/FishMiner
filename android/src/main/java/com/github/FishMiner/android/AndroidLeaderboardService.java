package com.github.FishMiner.android;

import com.github.FishMiner.data.ports.out.ILeaderBoardService;
import com.github.FishMiner.domain.ports.in.data.LeaderboardCallback;
import com.github.FishMiner.data.ScoreEntry;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidLeaderboardService implements ILeaderBoardService {
    private final FirebaseFirestore db;

    public AndroidLeaderboardService() {
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
                    List<ScoreEntry> scoreEntries = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String username = document.getString("username");
                        Long scoreValue = document.getLong("score");
                        if (username != null && scoreValue != null) {
                            scoreEntries.add(new ScoreEntry(username, scoreValue.intValue()));
                        }
                    }
                    callback.onSuccess(scoreEntries);
                } else {
                    callback.onFailure(task.getException().getMessage());
                }
            });
    }

    @Override
    public void submitScore(String username, int score, LeaderboardCallback callback) {
        System.out.println(">>> [AndroidLeaderboardService] submitScore called for: " + username + " with score: " + score);
        db.collection("LeaderBoard").document(username)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Long existingScore = documentSnapshot.getLong("score");
                    if (existingScore == null || score > existingScore) {
                        // Update only if the new score is higher
                        db.collection("LeaderBoard").document(username)
                            .update("score", score)
                            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    } else {
                        // No update needed
                        callback.onSuccess(null);
                    }
                } else {
                    // Create new document with username as ID
                    Map<String, Object> scoreEntry = new HashMap<>();
                    scoreEntry.put("username", username);
                    scoreEntry.put("score", score);

                    db.collection("LeaderBoard").document(username)
                        .set(scoreEntry)
                        .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                }
            })
            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getUserScore(String username, LeaderboardCallback callback) {
        db.collection("LeaderBoard").document(username)
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists()) {
                    String name = document.getString("username");
                    Long score = document.getLong("score");
                    List<ScoreEntry> result = new ArrayList<>();
                    result.add(new ScoreEntry(name, score != null ? score.intValue() : 0));
                    callback.onSuccess(result);
                } else {
                    callback.onSuccess(new ArrayList<>());
                }
            })
            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

}
