package com.haoche51.bee.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.haoche51.bee.GlobalData;
import com.haoche51.bee.R;
import com.haoche51.bee.adapter.GuidePagerAdapter;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.SpUtils;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BeeActivity implements ViewPager.OnPageChangeListener {

  @BindView(R.id.vg_guide) ViewPager vgGuide;
  @BindView(R.id.tv_guide_go) TextView tvGuideGo;

  private List<ImageView> imageViews = new ArrayList<>();
  private int[] imageIds = { R.drawable.guide_01, R.drawable.guide_02, R.drawable.guide_03 };

  @OnClick(R.id.tv_guide_go) public void goMain() {
    Intent intent = new Intent(GlobalData.mContext, MainActivity.class);
    startActivity(intent);
    SpUtils.setHasLoadGuidePage();
    finish();
  }

  @Override void initViews() {
    for (int id : imageIds) {
      ImageView imageView = new ImageView(this);
      imageView.setBackgroundResource(id);
      imageViews.add(imageView);
    }

    vgGuide.setAdapter(new GuidePagerAdapter(imageViews));
    vgGuide.addOnPageChangeListener(this);
    vgGuide.setCurrentItem(0);
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    hideTitleBar();
  }

  @Override int getContentViewResourceId() {
    return R.layout.activity_guide;
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override public void onPageSelected(int position) {
    if (position == imageIds.length - 1) {
      tvGuideGo.setVisibility(View.VISIBLE);
    } else {
      tvGuideGo.setVisibility(View.GONE);
    }
  }

  @Override public void onPageScrollStateChanged(int state) {

  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  @Override protected void onResume() {
    super.onResume();
    BeeStatistics.onPageStart(getClass().getSimpleName());
  }

  @Override protected void onPause() {
    super.onPause();
    BeeStatistics.onPageEnd(getClass().getSimpleName());
  }
}
