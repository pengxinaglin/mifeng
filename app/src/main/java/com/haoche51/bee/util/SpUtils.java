package com.haoche51.bee.util;

import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haoche51.bee.GlobalData;
import com.haoche51.bee.entity.BrandEntity;
import com.haoche51.bee.entity.CityEntity;
import java.util.ArrayList;
import java.util.List;

public class SpUtils {

  private static Gson mGson = new Gson();

  private static final String SP_NAME = "bee_other";

  private static SharedPreferences getSP() {
    return GlobalData.mContext.getSharedPreferences(SP_NAME, 0);
  }

  private static void setString(String key, String value) {
    getSP().edit().putString(key, value).apply();
  }

  private static String getString(String key, String defaultValue) {
    return getSP().getString(key, defaultValue);
  }

  private static String getDefaultString(String key) {
    return getString(key, "");
  }

  private static void setInt(String key, int value) {
    getSP().edit().putInt(key, value).apply();
  }

  private static int getInt(String key, int defaultValue) {
    return getSP().getInt(key, defaultValue);
  }

  private static int getDefaultInt(String key) {
    return getInt(key, -1);
  }

  private static void setBoolean(String key, boolean value) {
    getSP().edit().putBoolean(key, value).apply();
  }

  private static boolean getBoolean(String key, boolean defaultValue) {
    return getSP().getBoolean(key, defaultValue);
  }

  private static boolean getDefaultBoolean(String key) {
    return getBoolean(key, false);
  }

  //private static void setLong(String key, long value) {
  //  getSP().edit().putLong(key, value).apply();
  //}
  //
  //private static long getLong(String key, long defaultValue) {
  //  return getSP().getLong(key, defaultValue);
  //}
  //
  //private static long getDefaultLong(String key) {
  //  return getLong(key, 0L);
  //}

  /** 是否加载过引导页 */
  //每升级一次版本,记得换一次key
  private final static String HAS_LOAD_GUIDE_PAGE = "HAS_LOAD_GUIDE_PAGE_1207";

  public static void setHasLoadGuidePage() {
    setBoolean(HAS_LOAD_GUIDE_PAGE, true);
  }

  public static boolean getHasLoadGuidePage() {
    return getDefaultBoolean(HAS_LOAD_GUIDE_PAGE);
  }

  /** 是否导入过车系表数据 */
  private final static String HAS_IMPORT_SERIES_TABLE = "HAS_IMPORT_SERIES_TABLE";

  public static void setHasImportSeriesTable(boolean b) {
    setBoolean(HAS_IMPORT_SERIES_TABLE, b);
  }

  public static boolean getHasImportSeriesTable() {
    return getDefaultBoolean(HAS_IMPORT_SERIES_TABLE);
  }

  /** 是否导入过品牌表数据 */
  private final static String HAS_IMPORT_BRAND_TABLE = "HAS_IMPORT_BRAND_TABLE";

  public static void setHasImportBrandTable() {
    setBoolean(HAS_IMPORT_BRAND_TABLE, true);
  }

  public static boolean getHasImportBrandTable() {
    return getDefaultBoolean(HAS_IMPORT_BRAND_TABLE);
  }

  /** 保存当前选择城市 */
  private final static String CURRENT_CITY = "CURRENT_CITY";

  public static void setCurrentCity(int city) {
    setInt(CURRENT_CITY, city);
  }

  public static int getCurrentCity() {
    return getInt(CURRENT_CITY, 0);
  }

  public static String getCurrentCityName() {
    return getCityNameById(getCurrentCity() + "");
  }

  /** 当前品牌表里的数据是哪个城市的数据 */
  private final static String LAST_CITY_FOR_BRAND = "LAST_CITY_FOR_BRAND";

  public static void setLastCityForBrand(int city) {
    setInt(LAST_CITY_FOR_BRAND, city);
  }

  public static int getLastCityForBrand() {
    return getDefaultInt(LAST_CITY_FOR_BRAND);
  }

  /** 热门品牌 */
  private final static String HOME_HOT_BRANDS = "HOME_HOT_BRANDS";

  public static void setHotBrands(List<BrandEntity> brands) {

    if (BeeUtils.isListEmpty(brands)) return;

    String str = mGson.toJson(brands, new TypeToken<List<BrandEntity>>() {
    }.getType());

    setString(HOME_HOT_BRANDS, str);
  }

  public static List<BrandEntity> getHotBrands() {
    List<BrandEntity> list;
    String str = getDefaultString(HOME_HOT_BRANDS);

    list = mGson.fromJson(str, new TypeToken<List<BrandEntity>>() {
    }.getType());

    return list == null ? new ArrayList<BrandEntity>() : list;
  }

  /** 保存当前的channel */
  private final static String CHANNEL_KEY = "CHANNEL_KEY";

  public static void setChannel(String channel) {
    setString(CHANNEL_KEY, channel);
  }

  public static String getChannel() {
    return getDefaultString(CHANNEL_KEY);
  }

  /** 搜索历史记录 */
  private final static String SEARCH_HISTORY = "SEARCH_HISTORY";

  private static void setSearchHistory(List<String> historyData) {
    if (historyData == null) return;
    String saveStr = mGson.toJson(historyData, new TypeToken<List<String>>() {
    }.getType());
    setString(SEARCH_HISTORY, saveStr);
  }

  public static List<String> getSearchHistory() {
    String str = getDefaultString(SEARCH_HISTORY);
    ArrayList<String> lists;
    if (TextUtils.isEmpty(str)) {
      lists = new ArrayList<>();
    } else {
      lists = mGson.fromJson(str, new TypeToken<List<String>>() {
      }.getType());
    }
    return lists;
  }

  public static void saveHistory(String history) {

    if (TextUtils.isEmpty(history)) return;

    List<String> lists = getSearchHistory();
    if (lists.contains(history)) {
      lists.remove(history);
    }
    lists.add(0, history);

    //判断最多缓存20条
    if (lists.size() > 6) {
      lists = lists.subList(0, 6);
    }

    setSearchHistory(lists);
  }

  /** ===================  城市   ==================== */
  private final static String SUPPORT_CITIES = "SUPPORT_CITIES";

  public static void setSupportCities(List<CityEntity> cities) {
    setString(SUPPORT_CITIES, mGson.toJson(cities));
  }

  public static List<CityEntity> getSupportCities() {
    String str = getDefaultString(SUPPORT_CITIES);
    if (!TextUtils.isEmpty(str)) {
      return mGson.fromJson(str, new TypeToken<List<CityEntity>>() {
      }.getType());
    } else {
      return new ArrayList<>();
    }
  }

  /** 存储热门城市 */
  private final static String SUPPORT_HOT_CITIES = "SUPPORT_HOT_CITIES";

  public static void setHotCities(List<CityEntity> cities) {
    setString(SUPPORT_HOT_CITIES, mGson.toJson(cities));
  }

  public static List<CityEntity> getHotCities() {
    String str = getDefaultString(SUPPORT_HOT_CITIES);
    if (!TextUtils.isEmpty(str)) {
      return mGson.fromJson(str, new TypeToken<List<CityEntity>>() {
      }.getType());
    } else {
      return new ArrayList<>();
    }
  }

  public static CityEntity queryByCityName(String city_name) {
    if (!TextUtils.isEmpty(city_name)) {
      //如果名称以市结尾去掉市
      if (city_name.endsWith("市")) {
        city_name = city_name.substring(0, city_name.indexOf("市"));
      }
      List<CityEntity> lists = getSupportCities();
      for (CityEntity en : lists) {
        String c_name = en.getCity_name();
        if (c_name.equals(city_name)) {
          return en;
        }
      }
    }

    return null;
  }

  private static String getCityNameById(String city_id) {
    String result = "全国";
    List<CityEntity> allCityEntities = getSupportCities();
    if (allCityEntities != null && !allCityEntities.isEmpty()) {
      for (CityEntity entity : allCityEntities) {
        int enid = entity.getCity_id();
        if (city_id.equals(String.valueOf(enid))) {
          result = entity.getCity_name();
          break;
        }
      }
    }

    return result;
  }
  /** ===================  城市   ==================== */

  /** 存储资讯过的车的所有id,上限100 */
  private final static String ASK_VEHICLES_ID = "ASK_VEHICLES_ID";

  public static void setAskVehicleIds(List<String> entities) {

    if (BeeUtils.isListEmpty(entities)) return;

    String str = mGson.toJson(entities, new TypeToken<List<String>>() {
    }.getType());

    setString(ASK_VEHICLES_ID, str);
  }

  public static List<String> getAskVehicleIds() {
    List<String> list;
    String str = getDefaultString(ASK_VEHICLES_ID);

    list = mGson.fromJson(str, new TypeToken<List<String>>() {
    }.getType());

    return list == null ? new ArrayList<String>() : list;
  }

  public static String getAskString() {
    return getDefaultString(ASK_VEHICLES_ID);
  }
}


