package com.github.FishMiner;

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Logger implements ApplicationLogger {
    private static Logger instance;
    private FileHandle logFile;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private Logger() {
        // Overwrite the previous log file each time the game is run.
        logFile = Gdx.files.local("game_log.txt");
        logFile.writeString("Game Log Started: " + getCurrentTime() + "\n", false);
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    private String getCurrentTime() {
        return dateFormat.format(new Date());
    }

    // Writes the message to the log file with a timestamp.
    private void write(String message) {
        String timestampedMessage = getCurrentTime() + " " + message + "\n";
        logFile.writeString(timestampedMessage, true);
    }

    @Override
    public void log(String tag, String message) {
        String logMessage = "[" + tag + "] " + message;
        System.out.println(logMessage);
        Gdx.app.log(tag, message);
        write(logMessage);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        log(tag, message);
        exception.printStackTrace();
        write("Exception: " + exception.getMessage());
    }

    @Override
    public void error(String tag, String message) {
        String errMessage = "[" + tag + "] ERROR: " + message;
        System.err.println(errMessage);
        Gdx.app.error(tag, message);
        write(errMessage);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        error(tag, message);
        exception.printStackTrace();
        write("Exception: " + exception.getMessage());
    }

    @Override
    public void debug(String tag, String message) {
        String debugMessage = "[" + tag + "] DEBUG: " + message;
        System.out.println(debugMessage);
        Gdx.app.debug(tag, message);
        write(debugMessage);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        debug(tag, message);
        exception.printStackTrace();
        write("Exception: " + exception.getMessage());
    }
}
