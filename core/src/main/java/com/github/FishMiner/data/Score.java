package com.github.FishMiner.data;

public class Score {
    private final String username;
    private final int score;

    public Score(String username, int score) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (score < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }
}
