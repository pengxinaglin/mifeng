package com.haoche51.bee.util;

import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.haoche51.bee.GlobalData;
import com.haoche51.bee.R;
import com.haoche51.bee.dao.BrandDAO;
import com.haoche51.bee.dao.FilterTerm;
import com.haoche51.bee.dao.SeriesDAO;
import com.haoche51.bee.entity.BrandEntity;
import com.haoche51.bee.entity.FilterSearchEntity;
import com.haoche51.bee.entity.KeyValueEntity;
import com.haoche51.bee.entity.SeriesEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FilterUtils {

  public static final int ALL = 0x12071;
  private static final int TODAY = 0x12072;

  private static Gson mGson = new Gson();
  private static final String FILTER_SP_NAME = "bee_filter_sp_name";

  private static FilterTerm defaultFilterTerm = new FilterTerm();

  private static SharedPreferences getSP() {
    return GlobalData.mContext.getSharedPreferences(FILTER_SP_NAME, 0);
  }

  private static void setString(String key, String value) {
    getSP().edit().putString(key, value).apply();
  }

  private static String getString(String key, String defaultValue) {
    return getSP().getString(key, defaultValue);
  }

  /** 存储全部好车的筛选项 */
  private final static String ALL_GOOD_FILTER_TERM = "ALL_GOOD_FILTER_TERM";

  private static void setNormalFilterTerm(FilterTerm term) {
    setString(ALL_GOOD_FILTER_TERM, mGson.toJson(term));
  }

  private static FilterTerm getNormalFilterTerm() {
    String normalStr = getString(ALL_GOOD_FILTER_TERM, "");
    if (TextUtils.isEmpty(normalStr)) {
      return new FilterTerm();
    }
    return mGson.fromJson(normalStr, FilterTerm.class);
  }

  /** 存储今日新上的筛选项 */
  private final static String TODAY_FILTER_TERM = "TODAY_FILTER_TERM";

  private static void setTodayFilterTerm(FilterTerm term) {
    setString(TODAY_FILTER_TERM, mGson.toJson(term));
  }

  private static FilterTerm getTodayFilterTerm() {
    String normalStr = getString(TODAY_FILTER_TERM, "");
    if (TextUtils.isEmpty(normalStr)) {
      return new FilterTerm();
    }
    return mGson.fromJson(normalStr, FilterTerm.class);
  }

  /** 根据type决定的是哪个筛选项 */
  public static void setFilterTerm(FilterTerm term, int type) {
    switch (type) {
      case ALL:
        setNormalFilterTerm(term);
        break;
      case TODAY:
        setTodayFilterTerm(term);
        break;
    }
  }

  public static FilterTerm getFilterTerm(int type) {
    switch (type) {
      case ALL:
        return getNormalFilterTerm();
      case TODAY:
        return getTodayFilterTerm();
      default:
        return new FilterTerm();
    }
  }

  public static void resetFilterTerm(int type) {
    switch (type) {
      case ALL:
        setNormalFilterTerm(defaultFilterTerm);
        break;
      case TODAY:
        setTodayFilterTerm(defaultFilterTerm);
        break;
    }
  }

  public static boolean isCurrentDefaultCondition() {
    return defaultFilterTerm.equals(getFilterTerm(ALL));
  }

  public static void saveBrandFilterTerm(int type, int brand_id, int class_id) {
    FilterTerm term = getFilterTerm(type);
    term.setBrand_id(brand_id);
    term.setClass_id(class_id);
    setFilterTerm(term, type);
  }

  public static void saveTermPrice(int type, float lowPrice, float highPrice) {
    FilterTerm term = getFilterTerm(type);
    term.setLowPrice(lowPrice);
    term.setHighPrice(highPrice);
    setFilterTerm(term, type);
  }

  public static void saveSort(int type, String value) {
    String order = FilterUtils.getPointOrder(value);
    int sort = FilterUtils.getPointSort(value);
    FilterTerm term = FilterUtils.getFilterTerm(type);
    term.setSort(sort);
    term.setOrder(order);
    term.setDescriptionSort(value);
    setFilterTerm(term, type);
  }

  private final static String[] SORT_STR = BeeUtils.getResArray(R.array.bee_filter_sort);
  private static final String SORT_TYPE_REGISTER_TIME = "register_time";
  private static final String SORT_TYPE_PRICE = "sell_price";
  private static final String SORT_TYPE_MILES = "miles";
  private static final String SORT_TYPE_TIME = "refresh_time";

  /** 0 升序 */
  private static final int ORDER_ASC = 0;
  /** 1 降序 */
  private static final int ORDER_DESC = 1;

  private static String getPointOrder(String sortType) {
    if (SORT_STR[0].equals(sortType)) {
      //智能排序
      return SORT_TYPE_TIME;
    } else if (SORT_STR[1].equals(sortType)) {
      //价格低到高
      return SORT_TYPE_PRICE;
    } else if (SORT_STR[2].equals(sortType)) {
      //价格高到低
      return SORT_TYPE_PRICE;
    } else if (SORT_STR[3].equals(sortType)) {
      //车龄新到旧
      return SORT_TYPE_REGISTER_TIME;
    } else if (SORT_STR[4].equals(sortType)) {
      //里程短到长
      return SORT_TYPE_MILES;
    } else {
      return SORT_TYPE_TIME;
    }
  }

  private static int getPointSort(String sortType) {
    if (SORT_STR[0].equals(sortType)) {
      //智能排序
      return ORDER_DESC;
    } else if (SORT_STR[1].equals(sortType)) {
      //价格低到高
      return ORDER_ASC;
    } else if (SORT_STR[2].equals(sortType)) {
      //价格高到低
      return ORDER_DESC;
    } else if (SORT_STR[3].equals(sortType)) {
      //车龄新到旧
      return ORDER_DESC;
    } else if (SORT_STR[4].equals(sortType)) {
      //里程短到长
      return ORDER_ASC;
    } else {
      return ORDER_DESC;
    }
  }

  public static void priceKey2FilterTerm(int type, String key) {
    int[][] values = {
        { 0, 3 }, { 3, 5 }, { 5, 10 }, { 10, 15 }, { 15, 20 }, { 20, 30 }, { 30, 50 }, { 50, 0 }
    };

    String[] prices = BeeUtils.getResArray(R.array.bee_filter_price);

    int index = -1;
    for (int i = 0; i < prices.length; i++) {
      if (prices[i].equals(key)) {
        index = i;
        break;
      }
    }
    if (index >= 0) {
      int[] dest = values[index];
      saveTermPrice(type, dest[0], dest[1]);
    }
  }

  private static final String CHAR_SPLIT = "~";
  private static final String STR_YEAR = "年";
  private static final String STR_WAN = "万";
  private static final String STR_MILES = "公里";
  private static final String STR_ABOVE = "以上";
  private static final String STR_UNLIMITED = "不限";

  public static String getPrice(int low, int high) {
    if (low >= 0 && high > 0) {
      return low + CHAR_SPLIT + high + STR_WAN;
    } else if (low > 0 && high == 0) {
      return low + STR_WAN + STR_ABOVE;
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getCarAge(int low, int high) {
    if (low >= 0 && high > 0) {
      return low + CHAR_SPLIT + high + STR_YEAR;
    } else if (low > 0 && high == 0) {
      return low + STR_YEAR + STR_ABOVE;
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getDistance(int low, int high) {
    if (low >= 0 && high > 0) {
      return low + CHAR_SPLIT + high + STR_WAN + STR_MILES;
    } else if (low > 0 && high == 0) {
      return low + STR_WAN + STR_MILES + STR_ABOVE;
    } else {
      return STR_UNLIMITED;
    }
  }

  /** 排序类型 */
  public static String getPointOrder(int type) {
    return getFilterTerm(type).getOrder();
  }

  /** 升序还是降序 */
  public static int getPointSort(int type) {
    return getFilterTerm(type).getSort();
  }

  /** 将FilterTerm中的筛选项变成请求的参数 */
  public static Map<String, Object> getQuery(int type) {

    FilterTerm term = getFilterTerm(type);

    Map<String, Object> map = new HashMap<>();

    //城市
    if (SpUtils.getCurrentCity() > 0) {
      map.put("city_id", SpUtils.getCurrentCity());
    }

    //品牌
    if (term.getBrand_id() > 0) {
      map.put("brand_id", term.getBrand_id());
    }

    //车系
    if (term.getClass_id() > 0) {
      map.put("series_id", term.getClass_id());
    }

    //价格区间
    float lPrice = term.getLowPrice();
    float hPrice = term.getHighPrice();

    if (!(lPrice == 0F && hPrice == 0F)) {
      Number[] price = toArray(lPrice, hPrice);
      map.put("sell_price", price);
    }

    //车身结构
    int structure = term.getStructure();
    switch (structure) {
      case 1:
        map.put("vehicle_structure", "SUV");
        break;
      case 2:
        map.put("vehicle_structure", "MPV");
        break;
      case 3:
        map.put("vehicle_structure", "跑车");
        break;
    }

    //车龄
    int lYear = term.getFrom_year();
    int hYear = term.getTo_year();

    if (!(lYear == 0 && hYear == 0)) {
      long time[] = BeeUtils.getYearInterval(lYear, hYear);
      Number[] year = toArray(time[0], time[1]);
      map.put("register_time", year);
    }

    //里程
    float lMile = term.getFrom_miles();
    float hMile = term.getTo_miles();

    if (!(lMile == 0F && hMile == 0F)) {
      Number[] mile = toArray(lMile, hMile);
      map.put("miles", mile);
    }

    //变速箱
    switch (term.getGearboxType()) {
      case 1: //手动
        map.put("gearbox", "2");
        break;
      case 2://自动
        map.put("gearbox", "1");
        break;
    }

    //平台
    List<String> platform = term.getPlatform();
    if (!BeeUtils.isListEmpty(platform)) {
      map.put("platform", toPlatForm(platform));
    }

    return map;
  }

  private static Number[] toArray(Number... n) {
    Number[] numbers = new Number[2];
    if (n != null && n.length == 2) {
      if (n[1].intValue() == 0) {
        n[1] = 1000;
      }
      numbers[0] = n[0];
      numbers[1] = n[1];
    }
    return numbers;
  }

  private static Number[] toPlatForm(List<String> list) {
    String[] platformNumber = BeeUtils.getResArray(R.array.bee_filter_platform);
    Number[] numbers = new Number[list.size()];
    for (int i = 0; i < list.size(); i++) {
      String p = list.get(i);
      for (int j = 0; j < platformNumber.length; j++) {
        if (p.equals(platformNumber[j])) {
          numbers[i] = j;
          break;
        }
      }
    }
    return numbers;
  }

  public static String getFilterTermString(FilterTerm term, int type) {
    String[] speedBoxes = BeeUtils.getResArray(R.array.bee_filter_speed_box);
    String[] carTypes = BeeUtils.getResArray(R.array.bee_filter_car_type);
    String str = "";
    switch (type) {
      case BeeConstants.FILTER_PRICE:
        int priceLow = (int) term.getLowPrice();
        int priceHigh = (int) term.getHighPrice();
        if (priceLow == 0 && priceHigh == 0) {
          str = STR_UNLIMITED;
        } else if (priceLow != 0 && priceHigh == 0) {
          str = priceLow + STR_WAN + STR_ABOVE;
        } else {
          str = priceLow + CHAR_SPLIT + priceHigh + STR_WAN;
        }
        break;
      case BeeConstants.FILTER_SPEED_BOX:
        int gearboxType = term.getGearboxType();
        str = speedBoxes[gearboxType];
        break;
      case BeeConstants.FILTER_CAR_TYPE:
        int structure = term.getStructure();
        str = carTypes[structure];
        break;
    }
    return str;
  }

  /** 获取默认排序 数组内容 */
  public static List<KeyValueEntity> getDefaultSortData(int sortType) {
    int res = -1;
    switch (sortType) {
      case BeeConstants.FILTER_SORT:
        res = R.array.bee_filter_sort;
        break;
      case BeeConstants.FILTER_PRICE:
        res = R.array.bee_filter_price;
        break;
      case BeeConstants.FILTER_CAR_TYPE:
        res = R.array.bee_filter_more_car_type;
        break;
      case BeeConstants.FILTER_SPEED_BOX:
        res = R.array.bee_filter_more_speed_box;
        break;
      case BeeConstants.FILTER_PLATFORM:
        res = R.array.bee_filter_more_platform;
        break;
    }

    List<KeyValueEntity> mList = new ArrayList<>();
    if (res > 0) {
      String[] arr = BeeUtils.getResArray(res);
      for (String str : arr) {
        mList.add(new KeyValueEntity(str));
      }
    }
    return mList;
  }

  //搜索只能搜索品牌车系,价格
  public static void saveQueryToFilterTerm(FilterSearchEntity query) {
    if (query == null) return;
    FilterTerm filterTerm = getNormalFilterTerm();

    int brand_id = BeeUtils.str2Int(query.getBrand_id());
    int class_id = BeeUtils.str2Int(query.getClass_id());

    if (brand_id > 0) {

      filterTerm.setBrand_id(brand_id);
      if (class_id > 0) {
        filterTerm.setClass_id(class_id);
      }
    }

    //价格
    List<String> ps = query.getPrice();
    if (ps != null && ps.size() == 2) {
      int l_price = BeeUtils.str2Int(ps.get(0));
      int h_price = BeeUtils.str2Int(ps.get(1));
      filterTerm.setLowPrice(l_price);
      filterTerm.setHighPrice(h_price);
    }

    //车身结构
    // TODO: 17/1/13  需要对接
    //if (!TextUtils.isEmpty(query.getStructure())) {
    //  int structure = BeeUtils.str2Int(query.getStructure());
    //  filterTerm.setStructure(structure);
    //}

    setNormalFilterTerm(filterTerm);
  }

  public static boolean isMoreDefault(FilterTerm term) {
    boolean result = true;
    if (term.getFrom_year() != 0 || term.getTo_year() != 0) {
      result = false;
    } else if (term.getFrom_miles() != 0 || term.getTo_miles() != 0) {
      result = false;
    } else if (term.getGearboxType() != 0) {
      result = false;
    } else if (term.getStructure() != 0) {
      result = false;
    } else if (!BeeUtils.isListEmpty(term.getPlatform())) {
      result = false;
    }
    return result;
  }

  public static TreeMap<Integer, String> getConditions() {
    TreeMap<Integer, String> map = new TreeMap<>();
    FilterTerm term = getFilterTerm(ALL);

    //品牌
    String brandName = null;
    String className = null;

    if (term.getClass_id() > 0) {
      SeriesEntity se = SeriesDAO.getInstance().findSeriesById(term.getClass_id());
      if (se != null) {
        className = se.getName();
        brandName = se.getBrand_name();
      }
    }

    if (TextUtils.isEmpty(brandName)) {
      int brand_id = term.getBrand_id();
      if (brand_id > 0) {
        if (BrandDAO.getInstance().get(brand_id) != null) {
          BrandEntity entity = (BrandEntity) BrandDAO.getInstance().get(brand_id);
          brandName = entity.getBrand_name();
        }
      }
    }

    if (!TextUtils.isEmpty(brandName)) {
      map.put(BeeConstants.FILTER_BRAND, brandName);
    }

    if (!TextUtils.isEmpty(className)) {
      map.put(BeeConstants.FILTER_SERIES, className);
    }

    //价格
    float l_price = term.getLowPrice();
    float h_price = term.getHighPrice();
    if (l_price >= 0 && h_price > 0) {
      map.put(BeeConstants.FILTER_PRICE, (int) l_price + CHAR_SPLIT + (int) h_price + STR_WAN);
    } else if (l_price > 0 && h_price == 0) {
      map.put(BeeConstants.FILTER_PRICE, (int) l_price + STR_WAN + STR_ABOVE);
    }

    //更多
    //车龄
    int fromYear = term.getFrom_year();
    int toYear = term.getTo_year();
    if (fromYear >= 0 && toYear > 0) {
      map.put(BeeConstants.FILTER_CAR_AGE, fromYear + CHAR_SPLIT + toYear + STR_YEAR);
    } else if (fromYear > 0 && toYear == 0) {
      map.put(BeeConstants.FILTER_CAR_AGE, fromYear + STR_YEAR + STR_ABOVE);
    }

    //里程
    int fromMiles = term.getFrom_miles();
    int toMiles = term.getTo_miles();
    if (fromMiles >= 0 && toMiles > 0) {
      map.put(BeeConstants.FILTER_DISTANCE, fromMiles + CHAR_SPLIT + toMiles + STR_WAN + STR_MILES);
    } else if (fromMiles > 0 && toMiles == 0) {
      map.put(BeeConstants.FILTER_DISTANCE, fromMiles + STR_WAN + STR_MILES + STR_ABOVE);
    }

    //变速箱
    int speed = term.getGearboxType();
    String[] speedBoxArr = BeeUtils.getResArray(R.array.bee_filter_speed_box);
    if (speed > 0 && speed < speedBoxArr.length) {
      map.put(BeeConstants.FILTER_SPEED_BOX, speedBoxArr[speed]);
    }

    //车身结构
    int structure = term.getStructure();
    String[] structureArr = BeeUtils.getResArray(R.array.bee_filter_car_type);
    if (structure > 0 && structure < structureArr.length) {
      map.put(BeeConstants.FILTER_CAR_TYPE, structureArr[structure]);
    }

    return map;
  }
}