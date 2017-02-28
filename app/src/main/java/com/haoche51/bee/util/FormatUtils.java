package com.haoche51.bee.util;

import android.text.TextUtils;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
  /** 获取车源名，解决排版混乱问题 */
  public static String getVehicleName(String input) {
    if (TextUtils.isEmpty(input)) return "";

    char[] c = input.toCharArray();
    for (int i = 0; i < c.length; i++) {
      if (c[i] == 12288) {
        c[i] = (char) 32;
        continue;
      }
      if (c[i] > 65280 && c[i] < 65375) c[i] = (char) (c[i] - 65248);
    }
    return new String(c);
  }

  /** 汽车价格展示格式 */
  public static String getSoldPriceFormat(double price) {
    double dp = ArithmeticUtils.round(price, 2);
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    String str = decimalFormat.format(dp);
    return String.valueOf(str);
  }

  /** 汽车详细信息展示 */
  private static final String VEHICLE_INFO_FORMAT_COMMON_NO_TIME = "%s万公里 · %s";
  private static final String VEHICLE_INFO_FORMAT_COMMON = "%s上牌 · %s万公里 · %s";

  public static String getVehicleFormat(String time, String miles, String gearbox) {
    if (TextUtils.isEmpty(time)) {
      return String.format(VEHICLE_INFO_FORMAT_COMMON_NO_TIME, miles, gearbox);
    } else {
      return String.format(VEHICLE_INFO_FORMAT_COMMON, formatTimestamp(time), miles, gearbox);
    }
  }

  private static String formatTimestamp(String time) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM", Locale.CHINA);
    return sdf.format(new Date(BeeUtils.str2Long(time + "000")));
  }
}
