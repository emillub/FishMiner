package com.github.FishMiner.android;

import com.github.FishMiner.data.services.ILeaderBoardService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AndroidLeaderBoardAPI implements ILeaderBoardService {
    private final FirebaseFirestore db;

    public AndroidLeaderBoardAPI() {
        db = FirebaseFirestore.getInstance();
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
