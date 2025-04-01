package com.github.FishMiner.android;

import java.util.HashMap;
import java.util.Map;

import com.github.FishMiner.data.ports.out.ILeaderBoardAPI;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AndroidLeaderBoardAPI implements ILeaderBoardAPI {
    private final FirebaseFirestore db;

    public AndroidLeaderBoardAPI() {
        db = FirebaseFirestore.getInstance();
    }

    public void submitScore(String username, int score) {
        Map<String, Object> scoreEntry = new HashMap<>();
        scoreEntry.put("username", username);
        scoreEntry.put("score", score);

        db.collection("LeaderBoard")
            .add(scoreEntry)  // Firestore automatically generates a unique document ID
            .addOnSuccessListener(documentReference ->
                System.out.println("Score added with ID: " + documentReference.getId()))
            .addOnFailureListener(e ->
                System.err.println("Error adding score: " + e.getMessage()));
    }

    public void getTopScores() {
        db.collection("LeaderBoard")
            .orderBy("score", Query.Direction.DESCENDING) // Sort by highest score
            .limit(10) // Get top 10 scores
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        System.out.println(document.getString("username") + ": " + document.getLong("score"));
                    }
                } else {
                    System.err.println("Error getting leaderboard: " + task.getException().getMessage());
                }
            });

    }
}
