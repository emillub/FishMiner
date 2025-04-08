package com.github.FishMiner.lwjgl3;

import com.github.FishMiner.data.Score;
import com.github.FishMiner.data.services.ILeaderBoardService;
import com.github.FishMiner.data.services.LeaderboardCallback;
import com.github.FishMiner.ui.controller.ScreenManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DesktopLeaderBoardAPI implements ILeaderBoardService {

    private final String FIREBASE_PROJECT_ID = "fishminer-482a2";

    @Override
    public void submitScore(String username, int score, LeaderboardCallback callback) {
        new Thread(() -> {
            try {
                DesktopLogInAPI loginAPI = (DesktopLogInAPI) ScreenManager.getInstance().getGame().getFirebase();
                String idToken = loginAPI.getIdToken();
                if (idToken == null) {
                    callback.onFailure("Login required to submit score.");
                    return;
                }

                String endpoint = "https://firestore.googleapis.com/v1/projects/" +
                    FIREBASE_PROJECT_ID + "/databases/(default)/documents/LeaderBoard";

                HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + idToken);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                JSONObject fields = new JSONObject();
                fields.put("username", new JSONObject().put("stringValue", username));
                fields.put("score", new JSONObject().put("integerValue", score));
                payload.put("fields", fields);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    callback.onSuccess(null);
                } else {
                    String errorMsg = readStream(conn.getErrorStream());
                    callback.onFailure("Submit failed: " + errorMsg);
                }

            } catch (Exception e) {
                callback.onFailure("Error submitting score: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void getTopScores(LeaderboardCallback callback) {
        new Thread(() -> {
            try {
                DesktopLogInAPI loginAPI = (DesktopLogInAPI) ScreenManager.getInstance().getGame().getFirebase();
                String idToken = loginAPI.getIdToken();

                String endpoint = "https://firestore.googleapis.com/v1/projects/" +
                    FIREBASE_PROJECT_ID + "/databases/(default)/documents:runQuery";

                HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                if (idToken != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + idToken);
                }

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
                String response = readStream(responseCode == 200 ? conn.getInputStream() : conn.getErrorStream());

                if (responseCode != 200) {
                    callback.onFailure("Failed to fetch scores: " + response);
                    return;
                }

                JSONArray results = new JSONArray(response);
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

    private String readStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

}
