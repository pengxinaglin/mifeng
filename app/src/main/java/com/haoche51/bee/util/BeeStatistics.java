package com.haoche51.bee.util;

import android.text.TextUtils;
import com.baidu.mobstat.StatService;
import com.haoche51.bee.BuildConfig;
import com.haoche51.bee.GlobalData;

public class BeeStatistics {

  public static void onPageStart(String tag) {
    if (TextUtils.isEmpty(tag)) return;

    if (BuildConfig.ENABLE_BAIDU_STAT) {
      StatService.onPageStart(GlobalData.mContext, tag);
    }
  }

  public static void onPageEnd(String tag) {
    if (TextUtils.isEmpty(tag)) return;

    if (BuildConfig.ENABLE_BAIDU_STAT) {
      StatService.onPageEnd(GlobalData.mContext, tag);
    }
  }

  private static void commonClick(String s, String value) {
    if (BuildConfig.ENABLE_BAIDU_STAT) {
      StatService.onEvent(GlobalData.mContext, s, value);
    }
  }

  public static void navigationBarClick(String value) {
    commonClick("navigationBarClick", value);
  }

  public static void closeClick(String value) {
    commonClick("closeClick", value);
  }

  public static void homePageClick(String value) {
    commonClick("homePageClick", value);
  }

  public static void vehicleListClick(String value) {
    commonClick("vehicleListClick", value);
  }

  public static void searchClick(String value) {
    commonClick("searchClick", value);
  }

  public static void sortClick(String value) {
    commonClick("sortClick", value);
  }

  public static void brandClick(String value) {
    commonClick("brandClick", value);
  }

  public static void priceClick(String value) {
    commonClick("priceClick", value);
  }

  public static void moreClick(String value) {
    commonClick("moreClick", value);
  }

  public static void VehicleDetailClick(String value) {
    commonClick("VehicleDetailClick", value);
  }
}
