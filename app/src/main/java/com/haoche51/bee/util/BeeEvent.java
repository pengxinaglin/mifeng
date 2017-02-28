package com.haoche51.bee.util;

import com.haoche51.bee.entity.EventBusEntity;
import org.greenrobot.eventbus.EventBus;

public class BeeEvent {

  /** 通知具体的筛选项被点击 */
  public static final String ACTION_SORT_CHOOSE = "ACTION_SORT_CHOOSE";
  public static final String ACTION_BRAND_CHOOSE = "ACTION_BRAND_CHOOSE";
  public static final String ACTION_CAR_SERIES_CHOOSE = "ACTION_CAR_SERIES_CHOOSE";
  public static final String ACTION_PRICE_CHOOSE = "ACTION_PRICE_CHOOSE";
  public static final String ACTION_MORE_CHOOSE = "ACTION_MORE_CHOOSE";

  /** 通知筛选项改变,筛选项的状态要做变化 */
  public static final String ACTION_BRAND_CHOOSE_CHANGE = "ACTION_BRAND_CHOOSE_CHANGE";
  public static final String ACTION_PRICE_CHOOSE_CHANGE = "ACTION_PRICE_CHOOSE_CHANGE";
  public static final String ACTION_MORE_CHOOSE_CHANGE = "ACTION_MORE_CHOOSE_CHANGE";
  public static final String ACTION_MORE_DISTANCE_CHOOSE_CHANGE = "DISTANCE_CHOOSE_CHANGE";
  public static final String ACTION_MORE_CAR_AGE_CHOOSE_CHANGE = "CAR_AGE_CHOOSE_CHANGE";

  /** 通知请求更多的筛选项的数据 */
  public static final String ACTION_REQUEST_MORE_DATA = "ACTION_REQUEST_MORE_DATA";

  /** 通知筛选条件变化 */
  public static final String ACTION_FILTER_CHANGE = "ACTION_FILTER_CHANGE";

  /** 通知展开还是折叠筛选栏 */
  public static final String ACTION_SHOW_BRAND_FRAGMENT = "ACTION_SHOW_BRAND_FRAGMENT";
  public static final String ACTION_HIDE_BRAND_FRAGMENT = "ACTION_HIDE_BRAND_FRAGMENT";
  public static final String ACTION_SHOW_SORT_FRAGMENT = "ACTION_SHOW_SORT_FRAGMENT";
  public static final String ACTION_HIDE_SORT_FRAGMENT = "ACTION_HIDE_SORT_FRAGMENT";
  public static final String ACTION_SHOW_PRICE_FRAGMENT = "ACTION_SHOW_PRICE_FRAGMENT";
  public static final String ACTION_HIDE_PRICE_FRAGMENT = "ACTION_HIDE_PRICE_FRAGMENT";
  public static final String ACTION_SHOW_MORE_FRAGMENT = "ACTION_SHOW_MORE_FRAGMENT";
  public static final String ACTION_HIDE_MORE_FRAGMENT = "ACTION_HIDE_MORE_FRAGMENT";

  /** 通知城市选择 */
  public static final String ACTION_SHOW_CITY_FRAGMENT = "ACTION_SHOW_CITY_FRAGMENT";
  public static final String ACTION_HIDE_CITY_FRAGMENT = "ACTION_HIDE_CITY_FRAGMENT";

  /** 通知城市变化 */
  public static final String ACTION_CITY_CHANGE = "ACTION_CITY_LOCATION_AGAIN";

  /** 通知平台选择 */
  public static final String ACTION_SHOW_PLATFORM_FRAGMENT = "ACTION_SHOW_PLATFORM_FRAGMENT";
  public static final String ACTION_HIDE_PLATFORM_FRAGMENT = "ACTION_HIDE_PLATFORM_FRAGMENT";
  public static final String ACTION_PLATFORM_CHOOSE_CHANGE = "ACTION_PLATFORM_CHOOSE_CHANGE";

  /** 搜索相关 */
  public static final String ACTION_GO_SEARCH = "ACTION_GO_SEARCH";
  public static final String ACTION_SEND_KEY_WORD = "ACTION_SEND_KEY_WORD";

  /** 切换底部tab */
  public static final String ACTION_CHANGE_MAIN_TAB = "ACTION_CHANGE_MAIN_TAB";

  /** 刷新列表,并重置filterBar颜色 */
  public static final String ACTION_REFRESH_VEHICLE_LIST = "ACTION_REFRESH_VEHICLE_LIST";

  public static void postEvent(EventBusEntity entity) {
    EventBus.getDefault().post(entity);
  }

  public static void postEvent(String action) {
    EventBusEntity entity = new EventBusEntity(action);
    postEvent(entity);
  }

  public static void postEvent(String action, Object obj) {
    EventBusEntity entity = new EventBusEntity(action, obj);
    postEvent(entity);
  }

  public static void postEvent(String action, String strValue) {
    EventBusEntity entity = new EventBusEntity(action, strValue);
    postEvent(entity);
  }

  public static void postEvent(String action, int intValue) {
    EventBusEntity entity = new EventBusEntity(action, intValue);
    postEvent(entity);
  }

  public static void postEvent(String action, int intValue, Object obj) {
    EventBusEntity entity = new EventBusEntity(action, intValue);
    entity.setObjValue(obj);
    postEvent(entity);
  }

  public static void cancelDelivery(EventBusEntity event) {
    EventBus.getDefault().cancelEventDelivery(event);
  }

  public static void register(Object subscriber) {
    if (subscriber == null) return;

    if (!EventBus.getDefault().isRegistered(subscriber)) {
      EventBus.getDefault().register(subscriber);
    }
  }

  public static void unRegister(Object subscribe) {
    if (subscribe == null) return;

    if (EventBus.getDefault().isRegistered(subscribe)) {
      EventBus.getDefault().unregister(subscribe);
    }
  }
}
