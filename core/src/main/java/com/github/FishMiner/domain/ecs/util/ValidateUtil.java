package com.github.FishMiner.domain.ecs.util;

public final class ValidateUtil {

    private ValidateUtil() {
        // Prevent instantiation
    }

    /**
     * Validates that none of the provided objects are null. You must provide an even number of arguments,
     * with each object immediately followed by its error message.
     *
     * @param args A sequence of object/message pairs.
     * @throws IllegalArgumentException if an odd number of arguments is provided.
     * @throws IllegalStateException if any object is null, with its corresponding error message.
     */
    public static void validateNotNull(Object... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("validateNotNull expects an even number of arguments: object/message pairs.");
        }
        for (int i = 0; i < args.length; i += 2) {
            Object object = args[i];
            String message = (args[i + 1] instanceof String) ? (String) args[i + 1] : "Argument at index " + i + " is null.";
            if (object == null) {
                throw new IllegalStateException(message);
            }
        }
    }
}
