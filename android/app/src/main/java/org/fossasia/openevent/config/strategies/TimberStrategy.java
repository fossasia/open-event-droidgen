package org.fossasia.openevent.config.strategies;

import android.content.Context;
import android.util.Log;

import org.fossasia.openevent.BuildConfig;
import org.fossasia.openevent.config.ConfigStrategy;

import timber.log.Timber;

public class TimberStrategy implements ConfigStrategy {

    @Override
    public boolean configure(Context context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        return false;
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FakeCrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t);
                }
            }
        }
    }

    private static final class FakeCrashLibrary {

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

}
