package com.haoche51.bee.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.haoche51.bee.GlobalData;
import com.haoche51.bee.R;
import com.haoche51.bee.dao.BrandDAO;
import com.haoche51.bee.dao.SeriesDAO;
import com.haoche51.bee.entity.BrandEntity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class DbUtils {

  public static void updateBrand(List<BrandEntity> brands) {
    if (brands != null && brands.size() != 0) {
      try {
        BrandDAO.getInstance().truncate();
        BrandDAO.getInstance().insert(brands);
      } catch (Exception e) {
        //
      }
    }
  }

  private final static String PREFIX =
      "insert into series(id,name,short_name,pinyin,brand_id,brand_name) values ";

  public static void insertSeriesData() {

    Runnable command = new Runnable() {
      public void run() {
        SeriesDAO.getInstance().truncate();
        Context context = GlobalData.mContext;
        BufferedReader br = null;
        boolean hasTransaction = false;
        try {
          SQLiteDatabase mDB = GlobalData.mDbHelper.getWritableDatabase();
          InputStream is = context.getResources().openRawResource(R.raw.auto_series);
          br = new BufferedReader(new InputStreamReader(is));
          String line;
          int insertCount = 0;
          while (!TextUtils.isEmpty((line = br.readLine()))) {
            if (insertCount == 0) {
              mDB.beginTransaction();
              hasTransaction = false;
            }
            String sql = PREFIX + line;
            mDB.execSQL(sql);
            insertCount++;
            if (insertCount == 100) {
              mDB.setTransactionSuccessful();
              mDB.endTransaction();
              insertCount = 0;
              hasTransaction = true;
            }
          }
          if (!hasTransaction) {
            mDB.setTransactionSuccessful();
            mDB.endTransaction();
          }
        } catch (Exception e) {
          //
        } finally {
          if (br != null) {
            try {
              br.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          // 到这里就是插入完毕了
          SpUtils.setHasImportSeriesTable(true);
        }
      }
    };

    ThreadUtils.execute(command);
  }

  /** 更新车系表 */
  //public static void updateSeries(final SeriesEntity entity) {
  //  ThreadUtils.execute(new Runnable() {
  //    @Override public void run() {
  //      try {
  //        if (entity != null) {
  //          int series_id = entity.getId();
  //          BaseEntity base = SeriesDAO.getInstance().get(series_id);
  //          if (base == null) {
  //            SeriesDAO.getInstance().insert(entity);
  //          }
  //        }
  //      } catch (Exception e) {
  //        e.printStackTrace();
  //      }
  //    }
  //  });
  //}
  public static String getBrandNameById(int brand_id) {
    if (0 != brand_id && BrandDAO.getInstance().get(brand_id) != null) {
      BrandEntity entity = (BrandEntity) BrandDAO.getInstance().get(brand_id);
      return entity.getBrand_name();
    }

    return "品牌不限";
  }

  //public static String getCarSeriesNameById(int series_id) {
  //  if (series_id != 0) {
  //    if (SeriesDAO.getInstance().get(series_id) != null) {
  //      SeriesEntity entity = (SeriesEntity) SeriesDAO.getInstance().get(series_id);
  //      return entity.getName();
  //    }
  //  }
  //  return "";
  //}
}
