package com.github.FishMiner.data.firebase;

import com.github.FishMiner.data.ports.out.LeaderBoardInterface;
import com.google.firebase.firestore.*;

public class LeaderBoard implements LeaderBoardInterface {
    FirebaseDatabase database;
    DatabaseReference highscores;
    private final FirebaseFirestore db;

    public LeaderBoard() {
        database = FirebaseDatabase.getInstance();
        highscores = database.getReference("highscores");
    }

    @Override
    public void submitHighscore(Score score) {
        highscores.push().setValue(score);
    }

    @Override
    public void getHighscores(ArrayList<Score> dataHolder) {
        System.out.println("Getting highscores");
        highscores.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                System.out.println("Got highscores");
                Iterable<DataSnapshot> response = task.getResult().getChildren();
                for (DataSnapshot child : response) {
                    dataHolder.add(child.getValue(Score.class));
                }
                Collections.sort(dataHolder);
            }
        });
    }


}
