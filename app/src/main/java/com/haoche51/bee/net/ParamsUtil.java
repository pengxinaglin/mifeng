package com.haoche51.bee.net;

import com.haoche51.bee.util.BeeConstants;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.SpUtils;
import java.util.HashMap;
import java.util.Map;

public class ParamsUtil {

  private static Map<String, Object> obtainParams() {
    return new HashMap<>();
  }

  /** 根据关键字获取联想建议 */
  public static Map<String, Object> getSuggestion(String keyword) {
    Map<String, Object> params = obtainParams();
    params.put("action", "suggestion");
    params.put("keyword", keyword);
    return params;
  }

  /** 获取热搜关键词列表 */
  public static Map<String, Object> getHotSearch() {
    Map<String, Object> param = obtainParams();
    param.put("action", "hotquery");
    return param;
  }

  /** 将关键词转化为筛选条件 */
  public static Map<String, Object> getSearchResult(String keyword) {
    Map<String, Object> param = obtainParams();
    param.put("action", "parse");
    param.put("keyword", keyword);
    return param;
  }

  /** 获取车源列表 */
  public static Map<String, Object> getVehicleSourceList(int page) {
    //这个接口页码从1开始
    page += 1;
    Map<String, Object> param = obtainParams();
    param.put("action", "list");
    param.put("page_num", page);
    param.put("page_size", BeeConstants.PAGE_SIZE);
    param.put("city_id", SpUtils.getCurrentCity());
    param.put("order", FilterUtils.getPointOrder(FilterUtils.ALL));
    param.put("desc", FilterUtils.getPointSort(FilterUtils.ALL));
    param.put("query", FilterUtils.getQuery(FilterUtils.ALL));
    return param;
  }

  /** 获取首页数据 */
  public static Map<String, Object> getHomeCityData() {
    Map<String, Object> params = obtainParams();
    params.put("action", "home");
    params.put("city_id", SpUtils.getCurrentCity());
    if (SpUtils.getCurrentCity() == 0) {
      params.put("city_id", "");
    }
    return params;
  }

  /** 获取品牌 */
  public static Map<String, Object> getSupportBrand() {
    Map<String, Object> params = obtainParams();
    params.put("action", "brands");
    params.put("city_id", SpUtils.getCurrentCity());
    if (SpUtils.getCurrentCity() == 0) {
      params.put("city_id", "");
    }
    return params;
  }

  /** 获取城市 */
  static Map<String, Object> getSupportCity() {
    Map<String, Object> params = obtainParams();
    params.put("action", "city");
    return params;
  }

  /** 获取我咨询过的车 */
  public static Map<String, Object> getMyAsk() {
    Map<String, Object> params = obtainParams();
    params.put("action", "mylist");
    params.put("ids", SpUtils.getAskVehicleIds().toArray());
    return params;
  }
}
