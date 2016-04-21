package org.sunger.net.exception;

import android.util.Log;

/**
 * Created by xuebp on 2016/1/26.
 */
public class Logger {
    private static boolean isLog = true;
    private static final String TAG = "OPlayer";

    public static void setLog(boolean isLog) {
        Logger.isLog = isLog;
    }

    public static boolean getIsLog() {
        return isLog;
    }

    public static void d(String tag, String msg) {
        if (isLog) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }


    public static void d(String tag, String msg, Throwable tr) {
        if (isLog) {
            Log.d(tag, msg, tr);
        }
    }

    public static void e(Throwable tr) {
        if (isLog) {
            Log.e(TAG, "", tr);
        }
    }

    public static void i(String msg) {
        if (isLog) {
            Log.i(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isLog) {
            Log.i(tag, msg);
        }
    }


    public static void i(String tag, String msg, Throwable tr) {
        if (isLog) {
            Log.i(tag, msg, tr);
        }

    }


    public static void e(String tag, String msg) {
        if (isLog) {
            Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        if (isLog) {
            Log.e(TAG, msg);
        }
    }


    public static void e(String tag, String msg, Throwable tr) {
        if (isLog) {
            Log.e(tag, msg, tr);
        }
    }

    public static void systemErr(String msg) {
        // if (true) {
        if (isLog) {
            if (msg != null) {
                Log.e(TAG, msg);
            }

        }
    }
}
