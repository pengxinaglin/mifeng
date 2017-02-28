package com.haoche51.bee;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import com.baidu.mobstat.StatService;
import com.haoche51.bee.util.BeeUtils;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;

public class BeeApplication extends Application {

  private static final String PACKAGE_NAME = "com.haoche51.bee";

  @Override public void onLowMemory() {
    super.onLowMemory();
    ImageLoader.getInstance().clearMemoryCache();
  }

  @Override public void onCreate() {
    super.onCreate();
    //确保只执行一次
    if (PACKAGE_NAME.equals(getAppNameByPID(this, Process.myPid()))) {
      doAppInit();
    }
  }

  private void doAppInit() {
    GlobalData.init(getApplicationContext());
    initImageLoader(getApplicationContext());
    StatService.setAppChannel(getApplicationContext(), BeeUtils.getCurrentChannel(), true);
    CrashReport.initCrashReport(getApplicationContext(), "8a5a230ae5", true);
  }

  private void initImageLoader(Context context) {
    ImageLoaderConfiguration config =
        new ImageLoaderConfiguration.Builder(context).threadPoolSize(3)// 线程池内加载的数量
            .threadPriority(Thread.NORM_PRIORITY - 1)
            .denyCacheImageMultipleSizesInMemory()
            .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
            .tasksProcessingOrder(QueueProcessingType.LIFO)
            .build();
    ImageLoader.getInstance().init(config);// 全局初始化此配置
  }

  public static String getAppNameByPID(Context context, int pid) {
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
      if (processInfo.pid == pid) {
        return processInfo.processName;
      }
    }
    return "";
  }
}
