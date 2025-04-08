package com.github.FishMiner.domain.ecs.util;

import com.github.FishMiner.Logger;

public final class ValidateUtil {
    private static final String TAG = "ValidateUtil";

    private ValidateUtil() {
        // Prevent instantiation
    }

    /**
     * Validates that none of the provided objects are null. You must provide an even number of arguments,
     * with each object immediately followed by its error message.
     *
     * @param args A sequence of object/message pairs.
     * @throws IllegalArgumentException if an odd number of arguments is provided.
     * @throws IllegalArgumentException if any object is null, with its corresponding error message.
     */
    public static void validateMultipleNotNull(Object... args) throws IllegalArgumentException, IllegalStateException {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("validateNotNull expects an even number of arguments: object/message pairs.");
        }
        for (int i = 0; i < args.length; i += 2) {
            Object object = args[i];
            String message = (args[i + 1] instanceof String) ? (String) args[i + 1] : "Argument at index " + i + " is null.";
            if (object == null) {
                IllegalStateException exception = new IllegalStateException(message);
                Logger.getInstance().error(TAG, "validateNotNull(): object was null. ", exception);
                throw exception;
            }
        }
    }

    /**
     * Null check for single object with name for easy debugging
     * @param arg the non-null object
     * @param argTag the field name of the object
     */
    public static void validateNotNull(Object arg, String argTag) {
        if (arg == null) {
            IllegalStateException exception = new IllegalStateException(argTag + "cannot be null");
            Logger.getInstance().error(TAG, "validateNotNull() caught exception: ", exception);
            throw exception;
        }
    }

    public static void validatePositiveNumbers(Number... args) throws IllegalArgumentException {
        String m1 = "Argument at index ";
        String m2 = " is negative: ";
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                throw new IllegalArgumentException(m1 + i + m2 + args[i]);
            }
            if (args[i].doubleValue() < 0) {
                IllegalArgumentException exception = new IllegalArgumentException(m1 + i + m2 + args[i]);
                Logger.getInstance().error(TAG, "validatePositiveNumbers(): ", exception);
                throw exception;
            }
        }
    }

    /**
     * Validator for integers >= 0 with name of variable for easy debugging
     * @param arg positive value
     * @param argTag the field name of the object
     * @throws IllegalArgumentException if check fails
     */
    public static void validatePositiveInt(int arg, String argTag) throws IllegalArgumentException {
        if (arg <= 0) {
            IllegalArgumentException exception = new IllegalArgumentException(argTag + " cannot be 0 or less. Was: " + arg);
            Logger.getInstance().error(TAG, "validatePositiveInt(): ", exception);
            throw exception;
        }
    }

    // TODO: uncomment, use or remove this
    //public static void validatePositiveInt(int arg, String argName) throws IllegalArgumentException {
    //    if (arg <= 0) {
    //        IllegalArgumentException exception = new IllegalArgumentException("Positive integer was negative.");
    //        Logger.getInstance().error(TAG, "validatePositiveInt(): ", exception);
    //        throw exception;
    //    }
    //}

    public static void validatePositiveFloat(float arg, String argTag) throws IllegalArgumentException {
        if (arg <= 0) {
            IllegalArgumentException exception = new IllegalArgumentException(argTag + " cannot be 0 or less.");
            Logger.getInstance().error(TAG, "validatePositiveFloat(): ", exception);
            throw exception;
        }
    }

    public static void validateNegativeFloat(float arg, String argTag) throws IllegalArgumentException {
        if (arg >= 0) {
            IllegalArgumentException exception = new IllegalArgumentException(argTag + " cannot be 0 or more.");
            Logger.getInstance().error(TAG, "validateNegativeFloat(): ", exception);
            throw exception;
        }
    }
    public static void validateMoreThanZero(Number... args) throws IllegalArgumentException {
        String m1 = "Argument at index ";
        String m2 = " is negative: ";
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                throw new IllegalArgumentException(m1 + i + m2 + args[i]);
            }
            if (args[i].doubleValue() <= 0) {
                IllegalArgumentException exception = new IllegalArgumentException(m1 + i + m2 + args[i]);
                Logger.getInstance().error(TAG, "validateMoreThanZero(): ", exception);
                throw exception;
            }
        }
    }
}
