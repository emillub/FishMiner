package com.github.FishMiner;

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Singleton logger class that implements LibGDX {@link ApplicationLogger}.
 * Logs messages with timestamps to a local file ("game_log.txt") and forwards
 * them to the default LibGDX logger and system console.
 * The log file is overwritten on each application start.
 */
public class Logger implements ApplicationLogger {
    private static Logger instance;
    private final FileHandle logFile;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the log file and writes a header with the current timestamp.
     *
     * If you want to prevent overwriting, you can code it here.
     */
    private Logger() {
        // Overwrite the previous log file each time the game is run.
        logFile = Gdx.files.local("game_log.txt");
        logFile.writeString("Game Log Started: " + dateFormat.format(new Date()) + "\n", false);
    }

    /**
     * Returns the singleton instance of the Logger.
     *
     * @return the Logger instance
     */
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }


    /**
    * Writes the message to the log file with a timestamp.
     *
     * @param message the message to log
    */
    private void write(String message) {
        String timestampedMessage = dateFormat.format(new Date()) + " " + message + "\n";
        logFile.writeString(timestampedMessage, true);
    }

    /**
     * Logs an informational message.
     *
     * @param tag     the log tag
     * @param message the log message
     */
    @Override
    public void log(String tag, String message) {
        String logMessage = "[" + tag + "] " + message;
        System.out.println(logMessage);
        Gdx.app.log(tag, message);
        write(logMessage);
    }

    /**
     * Logs an informational message along with an exception.
     *
     * @param tag       the log tag
     * @param message   the log message
     * @param exception the exception to log
     */
    @Override
    public void log(String tag, String message, Throwable exception) {
        log(tag, message);
        exception.printStackTrace();
        write("Exception: " + exception.getMessage());
    }

    /**
     * Logs an error message.
     *
     * @param tag     the log tag
     * @param message the error message
     */
    @Override
    public void error(String tag, String message) {
        String errMessage = "[" + tag + "] ERROR: " + message;
        System.err.println(errMessage);
        Gdx.app.error(tag, message);
        write(errMessage);
    }

    /**
     * Logs an error message with an exception.
     *
     * @param tag       the log tag
     * @param message   the error message
     * @param exception the exception to log
     */
    @Override
    public void error(String tag, String message, Throwable exception) {
        error(tag, message);
        exception.printStackTrace();
        write("Exception: " + exception.getMessage());
    }

    /**
     * Logs a debug message.
     *
     * @param tag     the log tag
     * @param message the debug message
     */
    @Override
    public void debug(String tag, String message) {
        String debugMessage = "[" + tag + "] DEBUG: " + message;
        System.out.println(debugMessage);
        Gdx.app.debug(tag, message);
        write(debugMessage);
    }

    /**
     * Logs a debug message with an exception.
     *
     * @param tag       the log tag
     * @param message   the debug message
     * @param exception the exception to log
     */
    @Override
    public void debug(String tag, String message, Throwable exception) {
        debug(tag, message);
        exception.printStackTrace();
        write("Exception: " + exception.getMessage());
    }
}
