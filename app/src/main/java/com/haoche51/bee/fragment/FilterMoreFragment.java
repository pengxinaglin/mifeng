package com.haoche51.bee.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.haoche51.bee.R;
import com.haoche51.bee.adapter.FilterCarTypeAdapter;
import com.haoche51.bee.adapter.FilterPlatFormAdapter;
import com.haoche51.bee.adapter.FilterSpeedBoxAdapter;
import com.haoche51.bee.custom.BeeGridView;
import com.haoche51.bee.custom.BeeScrollView;
import com.haoche51.bee.custom.SeekBarPressure;
import com.haoche51.bee.custom.ViewClickListener;
import com.haoche51.bee.dao.FilterTerm;
import com.haoche51.bee.entity.BVehicleItemEntity;
import com.haoche51.bee.entity.EventBusEntity;
import com.haoche51.bee.entity.KeyValueEntity;
import com.haoche51.bee.net.API;
import com.haoche51.bee.net.GsonParse;
import com.haoche51.bee.net.HttpCallBack;
import com.haoche51.bee.net.ParamsUtil;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeConstants;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.ViewUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FilterMoreFragment extends BeeFragment {

  @BindView(R.id.gv_filter_car_type) BeeGridView carTypeGv;
  @BindView(R.id.gv_filter_speed_box) BeeGridView speedBoxGv;
  @BindView(R.id.gv_filter_platform) BeeGridView platformGv;
  @BindView(R.id.sb_filter_car_age) SeekBarPressure carAgeSb;
  @BindView(R.id.sb_filter_distance) SeekBarPressure distanceSb;

  @BindView(R.id.sv_filter_more) BeeScrollView mScrollView;
  @BindView(R.id.tv_filter_more_top_title) TextView mTopTitle;
  @BindView(R.id.v_filter_more_top_line) View mTopLine;
  @BindView(R.id.tv_filter_more_ensure) TextView mEnsureTv;

  @BindView(R.id.tv_sb_car_age_hint) TextView carAgeHintTv;
  @BindView(R.id.tv_sb_distance_hint) TextView distanceHintTv;

  @BindView(R.id.tv_custom_distance_hint) TextView mCustomDistanceHint;
  @BindView(R.id.tv_custom_car_age_hint) TextView mCustomCarAgeHint;

  private FilterCarTypeAdapter carTypeAdapter;
  private FilterSpeedBoxAdapter speedBoxAdapter;
  private FilterPlatFormAdapter platformAdapter;

  private int titleHeight = BeeUtils.getDimenPixels(R.dimen.px_64dp);
  private int color = BeeUtils.getResColor(R.color.common_text_gray);

  private static final int CAR_AGE_VALUE = 11;
  private static final int DISTANCE_VALUE = 16;

  private int carAgeLow;
  private int carAgeHigh;

  private int distanceLow;
  private int distanceHigh;

  //纪录每次打开界面时的当前筛选条件,以便于不正常推出时的重置筛选条件到以前.
  private FilterTerm termTemp;

  private int type;

  public void setType(int type) {
    this.type = type;
  }

  public static FilterMoreFragment newInstance() {
    return new FilterMoreFragment();
  }

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_filter_more;
  }

  @Override void doInitViewOrData() {
    initCarTypeGv();
    initSpeedBoxGv();
    initPlatformGv();
    initCarAgeSb();
    initDistanceSb();
    initScroll();

    termTemp = FilterUtils.getFilterTerm(FilterUtils.ALL);
  }

  private void initScroll() {
    mScrollView.setScrollViewListener(new BeeScrollView.ScrollViewListener() {
      @Override
      public void onScrollChanged(BeeScrollView scrollView, int x, int y, int oldX, int oldY) {
        if (titleHeight - Math.abs(y) > 0) {
          mTopTitle.setVisibility(View.GONE);
          mTopLine.setVisibility(View.GONE);
        } else {
          mTopTitle.setVisibility(View.VISIBLE);
          mTopLine.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  private void initCarTypeGv() {
    int layoutId = R.layout.gvitem_filter_car_type;
    List<KeyValueEntity> data = FilterUtils.getDefaultSortData(BeeConstants.FILTER_CAR_TYPE);
    carTypeAdapter = new FilterCarTypeAdapter(getActivity(), data, layoutId, type);
    carTypeAdapter.setListener(listener);
    carTypeGv.setAdapter(carTypeAdapter);
  }

  private void initSpeedBoxGv() {
    int layoutId = R.layout.gvitem_filter_speed_box;
    List<KeyValueEntity> data = FilterUtils.getDefaultSortData(BeeConstants.FILTER_SPEED_BOX);
    speedBoxAdapter = new FilterSpeedBoxAdapter(getActivity(), data, layoutId, type);
    speedBoxAdapter.setListener(listener);
    speedBoxGv.setAdapter(speedBoxAdapter);
  }

  private void initPlatformGv() {
    int layoutId = R.layout.gvitem_filter_platform;
    List<KeyValueEntity> data = FilterUtils.getDefaultSortData(BeeConstants.FILTER_PLATFORM);
    platformAdapter = new FilterPlatFormAdapter(getActivity(), data, layoutId, type);
    platformAdapter.setListener(listener);
    platformGv.setAdapter(platformAdapter);
  }

  private void initCarAgeSb() {
    ViewUtils.changeTextViewColor(mCustomCarAgeHint, color, 2, 4);
    carAgeSb.setDefaultScale(CAR_AGE_VALUE);
    carAgeSb.setProgressHigh(CAR_AGE_VALUE);
    carAgeSb.setType(SeekBarPressure.AGE);
    carAgeSb.setOnSeekBarChangeListener(new SeekBarPressure.OnSeekBarChangeListener() {
      @Override public void onProgressBefore() {

      }

      @Override public void onProgressChanged(SeekBarPressure seekBar, double progressLow,
          double progressHigh) {
        carAgeLow = (int) progressLow;
        carAgeHigh = (int) progressHigh;
        if (carAgeHigh == CAR_AGE_VALUE) {
          carAgeHigh = 0;
        }
        setCarAgeSbHint(carAgeLow, carAgeHigh);
      }

      @Override public void onProgressAfter() {
        FilterTerm term = FilterUtils.getFilterTerm(FilterUtils.ALL);
        term.setFrom_year(carAgeLow);
        term.setTo_year(carAgeHigh);
        FilterUtils.setFilterTerm(term, FilterUtils.ALL);
        requestSearchData();
        BeeStatistics.moreClick("车龄" + carAgeLow + "~" + carAgeHigh);
      }
    });
  }

  private void setCarAgeSbHint(int low, int high) {
    String carAge = FilterUtils.getCarAge(low, high);
    if (BeeConstants.UNLIMITED.equals(carAge)) {
      carAgeHintTv.setTextColor(BeeUtils.getResColor(R.color.common_text_black));
      carAgeHintTv.setText(carAge);
    } else {
      carAgeHintTv.setTextColor(BeeUtils.getResColor(R.color.common_yellow));
      carAgeHintTv.setText(carAge);
    }
  }

  private void initDistanceSb() {
    ViewUtils.changeTextViewColor(mCustomDistanceHint, color, 2, 6);
    distanceSb.setDefaultScale(DISTANCE_VALUE);
    distanceSb.setProgressHigh(DISTANCE_VALUE);
    distanceSb.setType(SeekBarPressure.DISTANCE);
    distanceSb.setOnSeekBarChangeListener(new SeekBarPressure.OnSeekBarChangeListener() {
      @Override public void onProgressBefore() {

      }

      @Override public void onProgressChanged(SeekBarPressure seekBar, double progressLow,
          double progressHigh) {
        distanceLow = (int) progressLow;
        distanceHigh = (int) progressHigh;
        if (distanceHigh == DISTANCE_VALUE) {
          distanceHigh = 0;
        }
        setDistanceSbHint(distanceLow, distanceHigh);
      }

      @Override public void onProgressAfter() {
        FilterTerm term = FilterUtils.getFilterTerm(FilterUtils.ALL);
        term.setFrom_miles(distanceLow);
        term.setTo_miles(distanceHigh);
        FilterUtils.setFilterTerm(term, FilterUtils.ALL);
        requestSearchData();
        BeeStatistics.moreClick("里程" + distanceLow + "~" + distanceHigh);
      }
    });
  }

  private void setDistanceSbHint(int low, int high) {
    String distance = FilterUtils.getDistance(low, high);
    if (BeeConstants.UNLIMITED.equals(distance)) {
      distanceHintTv.setTextColor(BeeUtils.getResColor(R.color.common_text_black));
      distanceHintTv.setText(distance);
    } else {
      distanceHintTv.setTextColor(BeeUtils.getResColor(R.color.common_yellow));
      distanceHintTv.setText(distance);
    }
  }

  private ViewClickListener listener = new ViewClickListener() {
    @Override public void performViewClick(View v) {
      FilterTerm term = FilterUtils.getFilterTerm(FilterUtils.ALL);
      switch (v.getId()) {
        case R.id.rel_filter_car_type_item:
          int carType = (int) v.getTag(R.id.rel_filter_car_type_item);
          if (term.getStructure() == carType) {
            term.setStructure(0);
            BeeStatistics.moreClick("清除车身结构" + carType);
          } else {
            term.setStructure(carType);
            BeeStatistics.moreClick("添加车身结构" + carType);
          }
          carTypeAdapter.notifyDataSetChanged();
          break;
        case R.id.rel_filter_speed_box_item:
          int speedBox = (int) v.getTag(R.id.rel_filter_speed_box_item);
          if (term.getGearboxType() == speedBox) {
            term.setGearboxType(0);
            BeeStatistics.moreClick("清除变速箱" + speedBox);
          } else {
            term.setGearboxType(speedBox);
            BeeStatistics.moreClick("添加变速箱" + speedBox);
          }
          speedBoxAdapter.notifyDataSetChanged();
          break;
        case R.id.rel_filter_platform_item:
          List<String> list = term.getPlatform();
          if (list == null) list = new ArrayList<>();
          String platform = (String) v.getTag(R.id.rel_filter_platform_item);
          if (list.contains(platform)) {
            list.remove(platform);
            BeeStatistics.moreClick("清除平台" + platform);
          } else {
            list.add(platform);
            BeeStatistics.moreClick("添加平台" + platform);
          }
          term.setPlatform(list);
          platformAdapter.notifyDataSetChanged();
          break;
      }
      FilterUtils.setFilterTerm(term, FilterUtils.ALL);
      requestSearchData();
    }
  };

  private void resetDistanceSb() {
    distanceSb.setProgressHigh(DISTANCE_VALUE);
    distanceSb.setProgressLow(0);
    distanceHintTv.setTextColor(BeeUtils.getResColor(R.color.common_text_black));
    distanceHintTv.setText(BeeConstants.UNLIMITED);
  }

  private void resetCarAgeSb() {
    carAgeSb.setProgressHigh(CAR_AGE_VALUE);
    carAgeSb.setProgressLow(0);
    carAgeHintTv.setTextColor(BeeUtils.getResColor(R.color.common_text_black));
    carAgeHintTv.setText(BeeConstants.UNLIMITED);
  }

  @OnClick(R.id.tv_filter_more_top_reset) public void resetFilter() {
    FilterTerm term = FilterUtils.getFilterTerm(FilterUtils.ALL);
    term.setStructure(0);
    term.setFrom_year(0);
    term.setTo_year(0);
    term.setFrom_miles(0);
    term.setTo_miles(0);
    term.setGearboxType(0);
    term.setPlatform(new ArrayList<String>());
    FilterUtils.setFilterTerm(term, FilterUtils.ALL);
    carTypeAdapter.notifyDataSetChanged();
    speedBoxAdapter.notifyDataSetChanged();
    platformAdapter.notifyDataSetChanged();
    resetDistanceSb();
    resetCarAgeSb();
    requestSearchData();
    BeeStatistics.moreClick("全部重置");
  }

  @OnClick({ R.id.rel_filter_more_top, R.id.linear_filter_more_bottom }) public void setNull() {
  }

  @OnClick(R.id.iv_filter_more_close) public void hideMore() {
    FilterUtils.setFilterTerm(termTemp, FilterUtils.ALL);
    resetMoreStatus();
    BeeEvent.postEvent(BeeEvent.ACTION_HIDE_MORE_FRAGMENT);
    BeeStatistics.closeClick("更多筛选");
  }

  @OnClick(R.id.tv_filter_more_ensure) public void requestMoreData() {
    BeeEvent.postEvent(type + BeeEvent.ACTION_MORE_CHOOSE);
    BeeStatistics.moreClick("确定");
  }

  private void resetMoreStatus() {
    FilterTerm term = FilterUtils.getFilterTerm(FilterUtils.ALL);
    carTypeAdapter.notifyDataSetChanged();
    speedBoxAdapter.notifyDataSetChanged();
    platformAdapter.notifyDataSetChanged();
    distanceSb.setProgressLow(term.getFrom_miles());
    if (term.getTo_miles() == 0) {
      distanceSb.setProgressHigh(DISTANCE_VALUE);
    } else {
      distanceSb.setProgressHigh(term.getTo_miles());
    }
    setDistanceSbHint(term.getFrom_miles(), term.getTo_miles());
    carAgeSb.setProgressLow(term.getFrom_year());
    if (term.getTo_miles() == 0) {
      carAgeSb.setProgressHigh(CAR_AGE_VALUE);
    } else {
      carAgeSb.setProgressHigh(term.getTo_year());
    }
    setCarAgeSbHint(term.getFrom_year(), term.getTo_year());
  }

  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN)
  public void moreChooseChangeEvent(EventBusEntity entity) {
    String action = entity.getAction();
    if (action.equals(type + BeeEvent.ACTION_MORE_CHOOSE_CHANGE)) {
      carTypeAdapter.notifyDataSetChanged();
      speedBoxAdapter.notifyDataSetChanged();
      platformAdapter.notifyDataSetChanged();
    } else if (action.equals(type + BeeEvent.ACTION_MORE_DISTANCE_CHOOSE_CHANGE)) {
      resetDistanceSb();
    } else if (action.equals(type + BeeEvent.ACTION_MORE_CAR_AGE_CHOOSE_CHANGE)) {
      resetCarAgeSb();
    } else if (action.equals(type + BeeEvent.ACTION_REQUEST_MORE_DATA)) {
      termTemp = FilterUtils.getFilterTerm(FilterUtils.ALL);
      requestSearchData();
    }
  }

  private void requestSearchData() {
    Map<String, Object> params = ParamsUtil.getVehicleSourceList(0);
    API.post(params, new HttpCallBack() {
      @Override public void onFinish(String response) {
        if (TextUtils.isEmpty(response)) return;
        BVehicleItemEntity entity = GsonParse.parseGetVehicleSourceList(response);
        int count = BeeUtils.str2Int(entity.getCount());
        mEnsureTv.setText(BeeUtils.getResString(R.string.filter_more_search_result, count));
      }
    });
  }
}
