package org.fossasia.openevent;

/**
 * User: mohit
 * Date: 31/1/16
 */
public final class FakeCrashLibrary {

    /**
     * Not a real crash reporting library!
     */
    private FakeCrashLibrary() {
        throw new AssertionError("No instances.");
    }

    public static void log(int priority, String tag, String message) {
        // TODO add log entry to circular buffer.
    }

    public static void logWarning(Throwable t) {
        // TODO report non-fatal warning.
    }

    public static void logError(Throwable t) {
        // TODO report non-fatal error.
    }
}
