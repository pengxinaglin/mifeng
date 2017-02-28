package com.haoche51.bee.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class BeeChannelUtil {

  private static final String CHANNEL_KEY = "beeChannel";
  private static String mChannel;

  /** 返回市场。  如果获取失败返回defaultChannel */
  static String getChannel(Context context, String defaultChannel) {
    //内存中获取
    if (!TextUtils.isEmpty(mChannel)) {
      return mChannel;
    }
    //sp中获取
    mChannel = SpUtils.getChannel();
    if (!TextUtils.isEmpty(mChannel)) {
      return mChannel;
    }
    //从apk中获取
    mChannel = getChannelFromApk(context, CHANNEL_KEY);
    if (!TextUtils.isEmpty(mChannel)) {
      //保存sp中备用
      SpUtils.setChannel(mChannel);
      return mChannel;
    }
    //全部获取失败
    return defaultChannel;
  }

  /** 从apk中获取版本信息 */
  private static String getChannelFromApk(Context context, String channelKey) {
    //从apk包中获取
    ApplicationInfo appInfo = context.getApplicationInfo();
    String sourceDir = appInfo.sourceDir;
    //默认放在meta-inf/里， 所以需要再拼接一下
    String key = "META-INF/" + channelKey;
    String ret = "";
    ZipFile zipfile = null;
    try {
      zipfile = new ZipFile(sourceDir);
      Enumeration<?> entries = zipfile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = ((ZipEntry) entries.nextElement());
        String entryName = entry.getName();
        if (entryName.startsWith(key)) {
          ret = entryName;
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (zipfile != null) {
        try {
          zipfile.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    String[] split = ret.split("_");
    String channel = "";
    if (split.length >= 2) {
      channel = ret.substring(split[0].length() + 1);
    }
    return channel;
  }
}
