package com.haoche51.bee;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.haoche51.bee.entity.LocationEntity;
import com.haoche51.bee.util.BeeLog;

public class BeeService extends Service {

  public static final String TAG = "BeeService";

  private LocationEntity mLocEntity = null;
  private LocationClient mLocationClient = null;

  public void onCreate() {
    super.onCreate();
    initLocation();
  }

  private void initLocation() {

    mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类

    LocationClientOption option = new LocationClientOption();
    option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
    option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
    option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
    option.setOpenGps(true);// 可选，默认false,设置是否使用gps
    mLocationClient.setLocOption(option);
    mLocationClient.start();

    // 注册监听函数
    mLocationClient.registerLocationListener(new BDLocationListener() {
      @Override public void onReceiveLocation(BDLocation location) {
        if (location != null) {
          String city_name = location.getCity();
          double latitude = location.getLatitude();
          double longitude = location.getLongitude();
          BeeLog.d(TAG, "city_name == " + city_name);
          BeeLog.d(TAG, "latitude == " + latitude);
          BeeLog.d(TAG, "longitude == " + longitude);
          BeeLog.d(TAG, "type == " + location.getLocType());
          mLocEntity = new LocationEntity(latitude, longitude, city_name);
          if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
            mLocationClient = null;
          }
        }
      }
    });
  }

  @Override public IBinder onBind(Intent intent) {
    return new BeeServiceBinder();
  }

  private void stopCheck() {
    if (mLocationClient != null && mLocationClient.isStarted()) {
      mLocationClient.stop();
      mLocationClient = null;
    }
  }

  @Override public boolean onUnbind(Intent intent) {
    stopCheck();
    return super.onUnbind(intent);
  }

  public class BeeServiceBinder extends Binder {
    /** 获取百度定位城市 */
    public LocationEntity getBaiduLocation() {
      return mLocEntity;
    }

    /** 获取当前service对象 */
    public BeeService getPollService() {
      return BeeService.this;
    }
  }
}
