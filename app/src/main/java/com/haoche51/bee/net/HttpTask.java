package com.haoche51.bee.net;

import android.text.TextUtils;
import com.haoche51.bee.entity.BCityEntity;
import com.haoche51.bee.entity.BrandEntity;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.DbUtils;
import com.haoche51.bee.util.SpUtils;
import java.util.List;
import java.util.Map;

public class HttpTask {

  public static void updateBrand() {
    Map<String, Object> params = ParamsUtil.getSupportBrand();
    API.post(params, new HttpCallBack() {
      @Override public void onFinish(String response) {
        if (!TextUtils.isEmpty(response)) {
          List<BrandEntity> brands = GsonParse.parseBrand(response);
          if (!BeeUtils.isListEmpty(brands)) {
            DbUtils.updateBrand(brands);
            SpUtils.setHasImportBrandTable();
            SpUtils.setLastCityForBrand(SpUtils.getCurrentCity());
          }
        }
      }
    });
  }

  public static void updateCity() {
    Map<String, Object> params = ParamsUtil.getSupportCity();
    API.post(params, new HttpCallBack() {
      @Override public void onFinish(String response) {
        if (!TextUtils.isEmpty(response)) {
          BCityEntity entity = GsonParse.parseSupportCityData(response);
          SpUtils.setSupportCities(entity.getAll());
          SpUtils.setHotCities(entity.getHot());
        }
      }
    });
  }
}
