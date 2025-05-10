package com.github.FishMiner.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.github.FishMiner.data.ports.out.IAuthService;
import com.github.FishMiner.domain.ports.out.data.callbacks.FirebaseAuthCallback;
import com.github.FishMiner.domain.ports.out.data.interfaces.ILoginHandler;
import com.github.FishMiner.domain.ports.out.data.interfaces.IUserRegistrationHandler;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DesktopAuthService implements IAuthService, ILoginHandler, IUserRegistrationHandler {

    private final String API_KEY = "AIzaSyAvXR0t2vP72YM3Dg0wRh9RMYXigUvkElY" ;
    private String currentUsername = null;
    private String idToken = null;

    @Override
    public void login(String email, String password, FirebaseAuthCallback callback) {
        String endpoint = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
        authenticate(email, password, endpoint, callback);
    }

    @Override
    public void register(String email, String password, FirebaseAuthCallback callback) {
        String endpoint = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;
        authenticate(email, password, endpoint, callback);
    }

    private void authenticate(String email, String password, String endpoint, FirebaseAuthCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("email", email);
                payload.put("password", password);
                payload.put("returnSecureToken", true);

                OutputStream os = conn.getOutputStream();
                os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (responseCode == 200) {
                    currentUsername = jsonResponse.getString("email");
                    idToken = jsonResponse.getString("idToken"); //Store the token
                    Gdx.app.postRunnable(callback::onSuccess);
                } else {
                    String errorMessage = jsonResponse.getJSONObject("error").getString("message");
                    Gdx.app.postRunnable(() -> callback.onFailure(errorMessage));
                }

            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        }).start();
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername != null ? currentUsername : "Guest";
    }

    public String getIdToken() {
        return idToken;
    }
}
