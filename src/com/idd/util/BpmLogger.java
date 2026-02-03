package com.idd.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class BpmLogger {

    private static final Logger LOG =
        Logger.getLogger("BPM-DB-INVOKER");

    private BpmLogger() {}

    public static void info(String msg) {
        LOG.info(msg);
    }

    public static void warn(String msg) {
        LOG.warning(msg);
    }

    public static void error(String msg, Throwable t) {
        LOG.log(Level.SEVERE, msg, t);
    }
}
