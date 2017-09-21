package com.yan.sh.sh_android.util;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import timber.log.Timber;
/**
 * Created by yan on 6/11/17.
 */

public class TimberTree extends Timber.DebugTree {
    static Logger mLogger = LoggerFactory.getLogger(TimberTree.class);

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        String logMessage = tag + " : " + message;
        switch (priority) {
            case Log.DEBUG:
                mLogger.debug(logMessage);
                break;
            case Log.INFO:
                mLogger.info(logMessage);
                break;
            case Log.WARN:
                mLogger.warn(logMessage);
                break;
            case Log.ERROR:
                mLogger.error(logMessage);
                break;
            case Log.VERBOSE:
                return;
        }
    }
}
