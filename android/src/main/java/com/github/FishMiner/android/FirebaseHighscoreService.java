package com.github.FishMiner.android;

import com.github.FishMiner.data.services.IHighscoreService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHighscoreService implements IHighscoreService {

    private final FirebaseFirestore db;
    public FirebaseHighscoreService() {
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

}
