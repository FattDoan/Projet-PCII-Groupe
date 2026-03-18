package common;

/**
 * Validation runtime configurable.
 *
 * Par défaut, la validation est active.
 * Pour la desactiver au lancement JVM:
 * -Dpcii.validation.strict=false
 */
public final class Validation {
    private static volatile boolean strictValidationEnabled =
        Boolean.parseBoolean(System.getProperty("pcii.validation.strict", "true"));

    private Validation() {
    }

    public static boolean isStrictValidationEnabled() {
        return strictValidationEnabled;
    }

    public static void setStrictValidationEnabled(boolean enabled) {
        strictValidationEnabled = enabled;
    }

    public static void requireArgument(boolean condition, String message) {
        if (strictValidationEnabled && !condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireState(boolean condition, String message) {
        if (strictValidationEnabled && !condition) {
            throw new IllegalStateException(message);
        }
    }
}