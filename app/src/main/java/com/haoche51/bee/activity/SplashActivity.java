package com.haoche51.bee.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.haoche51.bee.BeeService;
import com.haoche51.bee.GlobalData;
import com.haoche51.bee.R;
import com.haoche51.bee.entity.BrandEntity;
import com.haoche51.bee.net.API;
import com.haoche51.bee.net.GsonParse;
import com.haoche51.bee.net.HttpCallBack;
import com.haoche51.bee.net.HttpTask;
import com.haoche51.bee.net.ParamsUtil;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.DbUtils;
import com.haoche51.bee.util.SpUtils;
import com.haoche51.bee.util.ViewUtils;
import java.util.List;
import java.util.Map;

public class SplashActivity extends BeeActivity {

  @BindView(R.id.frame_splash_bottom) FrameLayout mBottomFrame;
  @BindView(R.id.iv_splash_top) ImageView mCoreIv;

  private int sh = BeeUtils.getScreenHeightPixels();

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    hideTitleBar();
  }

  @Override int getContentViewResourceId() {
    return R.layout.activity_splash;
  }

  @Override void initViews() {

    startService(new Intent(this, BeeService.class));

    doResizeBottom();

    doSeeIfImportedSeries();

    doSeeIfImportedBrand();
  }

  @Override protected void onResume() {
    super.onResume();
    mCoreIv.postDelayed(new Runnable() {
      @Override public void run() {
        ViewUtils.animateLayout(mBottomFrame, 0, (int) (sh * 0.42F));
        ViewUtils.animateLayout(mCoreIv, -(int) (sh * 0.68F), 0);
      }
    }, 1000);
    mCoreIv.postDelayed(new Runnable() {
      @Override public void run() {
        doFinalGoMain();
      }
    }, 2200);
    BeeStatistics.onPageStart(getClass().getSimpleName());
  }

  private void doResizeBottom() {
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mCoreIv.getLayoutParams();
    params.bottomMargin = (int) (sh * 0.68F);
    params.topMargin = -(int) (sh * 0.68F);
    mCoreIv.setLayoutParams(params);
    mCoreIv.setVisibility(View.VISIBLE);
  }

  private void doSeeIfImportedSeries() {
    // 如果没有导入过车系表数据,导入
    if (!SpUtils.getHasImportSeriesTable()) {
      // 导入车系表数据
      DbUtils.insertSeriesData();
    }
  }

  private void doSeeIfImportedBrand() {
    // 如果没有导入过品牌数据,导入
    if (!SpUtils.getHasImportBrandTable()) {
      // 导入品牌数据
      HttpTask.updateBrand();
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  private void doFinalGoMain() {
    Class<?> cls = SpUtils.getHasLoadGuidePage() ? MainActivity.class : GuideActivity.class;
    Intent intent = new Intent(GlobalData.mContext, cls);
    startActivity(intent);
    finish();
    overridePendingTransition(R.anim.anim_main_enter, R.anim.anim_splash_exit);
  }

  @Override protected void onPause() {
    super.onPause();
    BeeStatistics.onPageEnd(getClass().getSimpleName());
  }
}
