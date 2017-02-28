package com.haoche51.bee.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import com.haoche51.bee.GlobalData;
import com.haoche51.bee.R;
import com.haoche51.bee.custom.NormalToast;
import com.haoche51.bee.net.HttpAddress;
import com.qihoo.appstore.common.updatesdk.lib.UpdateHelper;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BeeUtils {

  public static long now() {
    return System.currentTimeMillis();
  }

  public static long nowDiff(long input) {
    return now() - input;
  }

  /** 隐藏键盘 */
  public static void hideKeyboard(View view) {
    if (view != null) {
      InputMethodManager imm =
          (InputMethodManager) GlobalData.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  public static String getPackageName() {
    return GlobalData.mContext.getPackageName();
  }

  /** 获取当前渠道号 */
  public static String getCurrentChannel() {
    return BeeChannelUtil.getChannel(GlobalData.mContext, "1207000");
  }

  public static Resources getResources() {
    return GlobalData.mContext.getResources();
  }

  /** 判断当前是否有可用网络 */
  public static boolean isNetAvailable() {
    ConnectivityManager mConnectManager =
        (ConnectivityManager) GlobalData.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = mConnectManager.getActiveNetworkInfo();
    return info != null && info.isAvailable();
  }

  /** 获取当前版本号 */
  public static int getAppVersionCode() {
    int versionCode = 0;
    try {
      PackageManager mPackageManager = GlobalData.mContext.getPackageManager();
      PackageInfo mPackageInfo =
          mPackageManager.getPackageInfo(GlobalData.mContext.getPackageName(), 0);
      versionCode = mPackageInfo.versionCode;
    } catch (Exception e) {
      //
    }
    return versionCode;
  }

  /** 获取当前程序版本名称 */
  public static String getAppVersionName() {
    String versionName = "";
    try {
      PackageManager mPackageManager = GlobalData.mContext.getPackageManager();
      PackageInfo mPackageInfo =
          mPackageManager.getPackageInfo(GlobalData.mContext.getPackageName(), 0);
      versionName = mPackageInfo.versionName;
      if (versionName == null || versionName.length() <= 0) {
        versionName = "unknown";
      }
    } catch (Exception e) {
      //
    }
    return versionName;
  }

  public static void toastNetError() {
    BeeUtils.showToast(R.string.bee_net_unreachable);
  }

  /** 显示页码的toast */
  public static void showPageToast(int curPage, int totalPage) {
    curPage = curPage > totalPage ? totalPage : curPage;
    String text = curPage + "/" + totalPage;
    NormalToast toast = new NormalToast(text);
    toast.show();
  }

  public static void showCountToast(int totalCount) {
    String text = getResString(R.string.bee_total_vehicle, totalCount);
    final Toast toast = Toast.makeText(GlobalData.mContext, text, Toast.LENGTH_SHORT);
    TextView view = new TextView(GlobalData.mContext);
    int lrp = BeeUtils.getDimenPixels(R.dimen.px_6dp);
    int tbp = BeeUtils.getDimenPixels(R.dimen.px_2dp);
    view.setPadding(lrp, tbp, lrp, tbp);
    view.setGravity(Gravity.CENTER);
    view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
    view.setTextColor(Color.WHITE);
    view.setBackgroundResource(R.drawable.bg_custom_toast);
    view.setText(text);
    toast.setView(view);
    toast.setDuration(Toast.LENGTH_SHORT);
    int yOffset = BeeUtils.getDimenPixels(R.dimen.px_100dp);
    toast.setGravity(Gravity.TOP, 0, yOffset);
    toast.show();

    try {
      new Timer().schedule(new TimerTask() {
        @Override public void run() {
          toast.cancel();
        }
      }, 800);
    } catch (Exception e) {
      //
    }
  }

  public static void showToast(int resString) {
    if (resString <= 0) return;
    String toastStr = getResources().getString(resString);
    showToast(toastStr);
  }

  public static void showToast(String msg) {
    if (TextUtils.isEmpty(msg)) return;

    if (Looper.myLooper() == Looper.getMainLooper()) {
      Toast.makeText(GlobalData.mContext, msg, Toast.LENGTH_SHORT).show();
    } else {
      Looper.prepare();
      Toast.makeText(GlobalData.mContext, msg, Toast.LENGTH_SHORT).show();
      Looper.loop();
    }
  }

  /** 获取当前屏幕宽度 */
  public static int getScreenWidthInPixels() {
    return getResources().getDisplayMetrics().widthPixels;
  }

  /** 获取当前屏幕高度 */
  public static int getScreenHeightPixels() {
    return getResources().getDisplayMetrics().heightPixels;
  }

  public static int getDimenPixels(int resDimen) {
    return getResources().getDimensionPixelOffset(resDimen);
  }

  /** dp转px */
  public static int dp2px(float dpValue) {
    final float scale = getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /** 获取车辆详情wap地址 */
  public static String getCarDetailURL(int vehicleSourceId) {

    StringBuilder sb = new StringBuilder();
    sb.append(HttpAddress.WAP_SERVER);
    sb.append("beecar/");
    sb.append(vehicleSourceId).append(".html");
    //sb.append("?channel=app");
    //sb.append("&promote_id=").append(BeeUtils.getCurrentChannel());
    return sb.toString();
  }

  public static String convertImageURL(String url, int w, int h) {
    url = TextUtils.isEmpty(url) ? "" : url;
    String str = new StringBuilder(url).append("?imageView2")
        .append("/1/w/")
        .append(w)
        .append("/h/")
        .append(h)
        .toString();
    return str;
  }

  public static String averageImageURL(String url, int w, int h) {
    url = TextUtils.isEmpty(url) ? "" : url;
    String str = new StringBuilder(url).append("?imageView2")
        .append("/0/w/")
        .append(w)
        .append("/h/")
        .append(h)
        .toString();
    return str;
  }

  public static int str2Int(String str) {
    if (TextUtils.isEmpty(str) || !TextUtils.isDigitsOnly(str)) {
      return 0;
    }
    return Integer.parseInt(str);
  }

  public static float str2Float(String str) {
    if (TextUtils.isEmpty(str)) return 0F;
    return Float.parseFloat(str);
  }

  public static long str2Long(String str) {
    if (TextUtils.isEmpty(str)) return 0L;
    return Long.parseLong(str);
  }

  private static long getDiffTimeStamp(int diffYear) {
    long cur = System.currentTimeMillis();

    Calendar curCal = Calendar.getInstance();
    curCal.setTimeInMillis(cur);

    int curYear = curCal.get(Calendar.YEAR);

    int resultYear = curYear - diffYear;

    Calendar resultCal = Calendar.getInstance();

    resultCal.set(resultYear, curCal.get(Calendar.MONTH), 1, 0, 0, 0);

    return resultCal.getTimeInMillis();
  }

  public static long[] getYearInterval(int from_year, int to_year) {
    if (to_year == 0 && from_year != 0) {
      to_year = 30;
    }
    long result[] = new long[2];
    result[0] = getDiffTimeStamp(to_year) / 1000;
    result[1] = getDiffTimeStamp(from_year) / 1000;
    return result;
  }

  public static String getOSVersion() {
    return Build.VERSION.RELEASE;
  }

  public static Drawable getResDrawable(int resDrawableID) {
    return getResources().getDrawable(resDrawableID);
  }

  public static String getResString(int resID) {
    return getResources().getString(resID);
  }

  public static String[] getResArray(int res) {
    return getResources().getStringArray(res);
  }

  public static int getResColor(int color) {
    return getResources().getColor(color);
  }

  public static String getResString(int resID, Object... formatArgs) {
    return getResources().getString(resID, formatArgs);
  }

  public static void diaPhone(String phone) {
    if (TextUtils.isEmpty(phone)) return;
    try {
      Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      GlobalData.mContext.startActivity(intent);
    } catch (Exception e) {
    }
  }

  public static boolean isListEmpty(List<?> list) {
    return list == null || list.size() == 0;
  }

  public static String getCurDPI() {
    DisplayMetrics metrics = getResources().getDisplayMetrics();
    int mCurrentDensity = metrics.densityDpi;
    StringBuilder sb = new StringBuilder("now in ");
    if (mCurrentDensity > DisplayMetrics.DENSITY_XHIGH
        && mCurrentDensity <= DisplayMetrics.DENSITY_XXHIGH) {
      //xxh
      sb.append(" xxh ");
    } else if (mCurrentDensity > DisplayMetrics.DENSITY_HIGH
        && mCurrentDensity <= DisplayMetrics.DENSITY_XHIGH) {
      //xh
      sb.append(" xh ");
    } else if (mCurrentDensity <= DisplayMetrics.DENSITY_HIGH) {
      //h
      sb.append(" hdpi ");
    }
    return sb.toString();
  }

  public static Rect getTextRect(String text, float size) {
    Paint pFont = new Paint();
    pFont.setTextSize(size);
    Rect rect = new Rect();
    pFont.getTextBounds(text, 0, text.length(), rect);
    return rect;
  }

  //360升级
  public static void do360Update() {
    UpdateHelper.getInstance().init(GlobalData.mContext, getResColor(R.color.common_yellow));
    UpdateHelper.getInstance().setDebugMode(false);
    long intervalMillis = 10 * 1000L;           //第一次调用startUpdateSilent出现弹窗后，如果10秒内进行第二次调用不会查询更新
    UpdateHelper.getInstance().autoUpdate(getPackageName(), false, intervalMillis);
  }
}
