package com.beloo.widget.chipslayoutmanager.util.log;

import com.beloo.widget.chipslayoutmanager.BuildConfig;

import java.util.HashSet;
import java.util.Set;

/** this class with static methods created only for fast replace of default android log */
public class Log {

    private static LogSwitcher logSwitcher = new LogSwitcher();

    @SuppressWarnings("ConstantConditions")
    private static LogWrapper log = BuildConfig.isLogEnabled ? new AndroidLog() : new SilentLog();

    ///////////////////////////////////////////////////////////////////////////
    // default android log delegates
    ///////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("WeakerAccess")
    public static int d (String tag, String msg) {
        return log.d(tag, msg);
    }

    @SuppressWarnings("WeakerAccess")
    public static int v (String tag, String msg) {
        return log.v(tag, msg);
    }

    @SuppressWarnings("WeakerAccess")
    public static int w (String tag, String msg) {
        return log.w(tag, msg);
    }

    @SuppressWarnings("WeakerAccess")
    public static int i (String tag, String msg) {
        return log.i(tag, msg);
    }

    public static int e (String tag, String msg) {
        return log.e(tag, msg);
    }

    ///////////////////////////////////////////////////////////////////////////
    // android log delegates with switcher
    ///////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("WeakerAccess")
    public static int d (String tag, String msg, int logCode) {
        return logSwitcher.isEnabled(logCode) ? d(tag, msg) : 0;
    }

    @SuppressWarnings("WeakerAccess")
    public static int v (String tag, String msg, int logCode) {
        return logSwitcher.isEnabled(logCode) ? v(tag, msg) : 0;
    }

    @SuppressWarnings("WeakerAccess")
    public static int w (String tag, String msg, int logCode) {
        return logSwitcher.isEnabled(logCode) ? w(tag, msg) : 0;
    }

    @SuppressWarnings("WeakerAccess")
    public static int i (String tag, String msg, int logCode) {
        return logSwitcher.isEnabled(logCode) ? i(tag, msg) : 0;
    }

    public static void with(LogSwitcher logSwitcher) {
        Log.logSwitcher = logSwitcher;
    }

    public static class LogSwitcher {
        private Set<Integer> enabledLogs = new HashSet<>();

        boolean isEnabled(int logCode) {
            return enabledLogs.contains(logCode);
        }

        public LogSwitcher with(int logCode) {
            enabledLogs.add(logCode);
            return this;
        }

        public LogSwitcher without(int logCode) {
            enabledLogs.remove(logCode);
            return this;
        }
    }
}
