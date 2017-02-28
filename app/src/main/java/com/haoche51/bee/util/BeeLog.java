package com.haoche51.bee.util;

import android.util.Log;
import com.haoche51.bee.BuildConfig;

public class BeeLog {

  private final static String DEFAULT_TAG = "logtag";
  private final static String NET_TAG = "nettag";

  private final static int MAX_LENGTH = 4000;

  private static void log(String msg) {
    d(DEFAULT_TAG, msg);
  }

  public static void net(String msg) {
    d(NET_TAG, msg);
  }

  public static void d(String customTag, String msg) {

    if (!BuildConfig.LOG_DEBUG) {
      return;
    }

    if (msg.length() > MAX_LENGTH) {
      Log.d(customTag, msg.substring(0, MAX_LENGTH));
      log(msg.substring(MAX_LENGTH));
    } else {
      Log.d(customTag, msg);
    }
  }
}
