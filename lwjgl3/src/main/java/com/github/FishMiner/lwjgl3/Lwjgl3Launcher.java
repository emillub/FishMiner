package com.github.FishMiner.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.FishMiner.FishMinerGame;
//import com.github.FishMiner.data.ports.out.FirebaseInterface;
//import com.github.FishMiner.data.ports.out.LeaderboardInterface;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
        //FirebaseInterface firebase;
        //LeaderboardInterface leaderboard;
    }


    private static Lwjgl3Application createApplication() {
        DesktopLogInAPI desktopLogInAPI = new DesktopLogInAPI();
        DesktopLeaderBoardAPI desktopLeaderBoardAPI = new DesktopLeaderBoardAPI();
        return new Lwjgl3Application(new FishMinerGame(desktopLogInAPI, desktopLeaderBoardAPI), getDefaultConfiguration());
        //return new Lwjgl3Application(new FishMinerGame(firebase, leaderboard), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("FishMiner");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
        configuration.setWindowedMode(720, 1600);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
