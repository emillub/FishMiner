package com.github.FishMiner.lwjgl3;

import com.github.FishMiner.data.Score;
import com.github.FishMiner.data.ports.out.ILeaderBoardService;
import com.github.FishMiner.domain.ports.out.LeaderboardCallback;
import com.github.FishMiner.domain.managers.ScreenManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DesktopLeaderboardService implements ILeaderBoardService {

    private final String FIREBASE_PROJECT_ID = "fishminer-482a2";

    @Override
    public void submitScore(String username, int score, LeaderboardCallback callback) {
        new Thread(() -> {
            try {
                DesktopAuthService loginAPI = (DesktopAuthService) ScreenManager.getInstance().getGame().getAuthService();
                String idToken = loginAPI.getIdToken();
                if (idToken == null) {
                    callback.onFailure("No auth token available. Please log in.");
                    return;
                }

                String endpoint = "https://firestore.googleapis.com/v1/projects/" +
                    FIREBASE_PROJECT_ID + "/databases/(default)/documents/LeaderBoard";

                HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + idToken);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String json = "{\n" +
                    "  \"fields\": {\n" +
                    "    \"username\": { \"stringValue\": \"" + username + "\" },\n" +
                    "    \"score\": { \"integerValue\": \"" + score + "\" }\n" +
                    "  }\n" +
                    "}";

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    callback.onSuccess(null);
                } else {
                    InputStream errorStream = conn.getErrorStream();
                    String errorMsg = new BufferedReader(new InputStreamReader(errorStream))
                        .lines().reduce("", (acc, line) -> acc + line + "\n");
                    callback.onFailure("Error: " + errorMsg);
                }

            } catch (Exception e) {
                callback.onFailure(e.getMessage());
            }
        }).start();
    }

    @Override
    public void getTopScores(LeaderboardCallback callback) {
        new Thread(() -> {
            try {
                DesktopAuthService loginAPI = (DesktopAuthService) ScreenManager.getInstance().getGame().getAuthService();
                String idToken = loginAPI.getIdToken();
                if (idToken == null) {
                    callback.onFailure("No auth token. Please log in.");
                    return;
                }

                String endpoint = "https://firestore.googleapis.com/v1/projects/fishminer-482a2/databases/(default)/documents:runQuery";

                HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + idToken);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String query = "{\n" +
                    "  \"structuredQuery\": {\n" +
                    "    \"from\": [{\"collectionId\": \"LeaderBoard\"}],\n" +
                    "    \"orderBy\": [{\"field\": {\"fieldPath\": \"score\"}, \"direction\": \"DESCENDING\"}],\n" +
                    "    \"limit\": 10\n" +
                    "  }\n" +
                    "}";

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(query.getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                if (responseCode != 200) {
                    callback.onFailure("Failed to fetch scores: " + response.toString());
                    return;
                }

                // Parse JSON using org.json
                JSONArray results = new JSONArray(response.toString());
                List<Score> scoreList = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    if (!result.has("document")) continue;

                    JSONObject fields = result.getJSONObject("document").getJSONObject("fields");

                    String username = fields.getJSONObject("username").getString("stringValue");
                    int score = fields.getJSONObject("score").getInt("integerValue");

                    scoreList.add(new Score(username, score));
                }

                callback.onSuccess(scoreList);

            } catch (Exception e) {
                callback.onFailure("Error fetching scores: " + e.getMessage());
            }
        }).start();
    }

    // Utility method to extract field values from JSON without a library
    private String extractField(String json, String fieldName) {
        String key = "\"" + fieldName + "\":";
        int start = json.indexOf(key);
        if (start == -1) return "";
        int valueStart = json.indexOf(":", start) + 1;
        int valueEnd = json.indexOf("\"", valueStart + 2);
        return json.substring(valueStart, valueEnd).replaceAll("\"", "").trim();
    }

}
