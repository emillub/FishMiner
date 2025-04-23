package com.github.FishMiner.domain.model.session;

/**
 * UserSession is a simple in-memory session manager for tracking
 * the currently authenticated user during a game session.
 *
 * This class holds the user's email after a successful login or registration,
 * allowing other parts of the game (like the MenuScreen or Leaderboard)
 * to check if a user is logged in.
 *
 * Note: This does not persist across app restarts.
 */

public class UserSession {
    private static String currentUserEmail = null;
    public static void login(String email) {
        currentUserEmail = email;
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public static boolean isLoggedIn() {
        return currentUserEmail != null;
    }

    public static void logout() {
        currentUserEmail = null;
    }
}
