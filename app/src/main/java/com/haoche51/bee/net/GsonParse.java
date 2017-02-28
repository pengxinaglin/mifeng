package com.haoche51.bee.net;

import com.google.gson.Gson;
import com.haoche51.bee.entity.AskEntity;
import com.haoche51.bee.entity.BBCityEntity;
import com.haoche51.bee.entity.BBVehicleItemEntity;
import com.haoche51.bee.entity.BBrandEntity;
import com.haoche51.bee.entity.BCityEntity;
import com.haoche51.bee.entity.BHomeDataEntity;
import com.haoche51.bee.entity.BVehicleItemEntity;
import com.haoche51.bee.entity.BrandEntity;
import com.haoche51.bee.entity.HotSearchEntity;
import com.haoche51.bee.entity.SuggestionEntity;
import com.haoche51.bee.entity.HomeDataEntity;
import com.haoche51.bee.entity.BFilterSearchEntity;
import com.haoche51.bee.util.SpUtils;
import java.util.ArrayList;
import java.util.List;

public class GsonParse {
  private static Gson mGson = new Gson();

  /** 解析搜索建议 */
  public static SuggestionEntity parseSuggestion(String resp) {
    SuggestionEntity result = null;
    try {
      result = mGson.fromJson(resp, SuggestionEntity.class);
    } catch (Exception e) {
      //
    }
    return result == null ? new SuggestionEntity() : result;
  }

  /** 解析搜索热门 */
  public static HotSearchEntity parseHotSearch(String resp) {
    HotSearchEntity result = null;
    try {
      result = mGson.fromJson(resp, HotSearchEntity.class);
    } catch (Exception e) {
      //
    }
    return result == null ? new HotSearchEntity() : result;
  }

  /** 解析搜索车源接口 */
  public static BFilterSearchEntity parseSearchResult(String resp) {
    BFilterSearchEntity searchEntity = null;
    try {
      searchEntity = mGson.fromJson(resp, BFilterSearchEntity.class);
    } catch (Exception e) {
      //
    }
    return searchEntity == null ? new BFilterSearchEntity() : searchEntity;
  }

  /** 解析车源列表 */
  public static BVehicleItemEntity parseGetVehicleSourceList(String resp) {
    BVehicleItemEntity itemEntity = null;
    BBVehicleItemEntity entity;
    try {
      entity = mGson.fromJson(resp, BBVehicleItemEntity.class);
      if (entity != null) {
        itemEntity = entity.getData();
      }
    } catch (Exception e) {
      //
    }
    return itemEntity == null ? new BVehicleItemEntity() : itemEntity;
  }

  /** 解析获得的品牌,并存储前10个作为热门品牌 */
  public static List<BrandEntity> parseBrand(String result) {

    List<BrandEntity> mBrands = null;
    try {
      BBrandEntity entity = mGson.fromJson(result, BBrandEntity.class);
      if (entity != null) {
        mBrands = entity.getData();
        if (mBrands != null) {
          int size = mBrands.size();
          size = size >= 10 ? 10 : size;
          List<BrandEntity> sub = mBrands.subList(0, size);
          SpUtils.setHotBrands(sub);
        }
      }
    } catch (Exception e) {
      //
    }
    return mBrands == null ? new ArrayList<BrandEntity>() : mBrands;
  }

  /** 获取首页数据接口 */
  public static HomeDataEntity parseHomeCityData(String resp) {
    HomeDataEntity entity = null;
    try {
      BHomeDataEntity bEntity = mGson.fromJson(resp, BHomeDataEntity.class);
      if (bEntity != null) {
        entity = bEntity.getData();
      }
    } catch (Exception e) {
      //
    }
    return entity == null ? new HomeDataEntity() : entity;
  }

  /** 获取支持城市接口 */
  static BCityEntity parseSupportCityData(String resp) {
    BCityEntity entity = null;
    try {
      BBCityEntity bEntity = mGson.fromJson(resp, BBCityEntity.class);
      if (bEntity != null) {
        entity = bEntity.getData();
      }
    } catch (Exception e) {
      //
    }
    return entity == null ? new BCityEntity() : entity;
  }

  /** 获取我的咨询 */
  public static AskEntity parseAskData(String resp) {
    AskEntity entity = null;
    try {
      entity = mGson.fromJson(resp, AskEntity.class);
    } catch (Exception e) {
      //
    }
    return entity == null ? new AskEntity() : entity;
  }
}
