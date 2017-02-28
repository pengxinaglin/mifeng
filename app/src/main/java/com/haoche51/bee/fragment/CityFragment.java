package com.haoche51.bee.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.haoche51.bee.BeeService;
import com.haoche51.bee.R;
import com.haoche51.bee.activity.MainActivity;
import com.haoche51.bee.adapter.CityAdapter;
import com.haoche51.bee.adapter.CityGridViewAdapter;
import com.haoche51.bee.custom.BeeGridView;
import com.haoche51.bee.custom.SideBar;
import com.haoche51.bee.entity.CityEntity;
import com.haoche51.bee.entity.LocationEntity;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.ComparatorCity;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.SpUtils;
import java.util.Collections;
import java.util.List;

public class CityFragment extends BeeFragment implements View.OnClickListener {

  @BindView(R.id.lv_city_main) ListView mCityLv;
  @BindView(R.id.tv_toast_city) TextView mTvToast;
  @BindView(R.id.side_bar_city) SideBar mSideBar;

  private TextView currentTv;
  private TextView locationTv;

  CityGridViewAdapter gvAdapter;
  CityAdapter mAdapter;
  List<CityEntity> mCityData = SpUtils.getSupportCities();
  List<CityEntity> mHotCityData = SpUtils.getHotCities();

  private int titleHeight = BeeUtils.getDimenPixels(R.dimen.px_155dp);

  @OnClick({ R.id.rel_city_top, R.id.frame_city_parent }) public void setNull() {
  }

  @OnClick(R.id.iv_city_close) public void hideCity() {
    BeeEvent.postEvent(BeeEvent.ACTION_HIDE_CITY_FRAGMENT);
  }

  @Override boolean isNeedBindEventBus() {
    return false;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_city;
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      //定位城市,热门城市,列表城市
      case R.id.tv_location_city:
      case R.id.rel_city_item:
      case R.id.city_name:
        Object o = v.getTag();
        if (o != null) {
          CityEntity entity = (CityEntity) v.getTag();
          SpUtils.setCurrentCity(entity.getCity_id());
          resetCityName();
          BeeEvent.postEvent(BeeEvent.ACTION_CITY_CHANGE, entity.getCity_name());
        }
        break;
      //全国
      case R.id.tv_all_city:
        SpUtils.setCurrentCity(0);
        resetCityName();
        BeeEvent.postEvent(BeeEvent.ACTION_CITY_CHANGE, "全国");
        break;
    }
    BeeEvent.postEvent(BeeEvent.ACTION_HIDE_CITY_FRAGMENT);
  }

  @Override void doInitViewOrData() {
    if (mSideBar == null) return;
    mSideBar.setTextView(mTvToast);

    // 设置headerView必须在setAdapter之前
    if (!mCityData.isEmpty()) {
      setHeaderView();
    }

    List<CityEntity> mCityDataTemp = sortCity(mCityData);
    mAdapter = new CityAdapter(mCityDataTemp, this);
    mCityLv.setAdapter(mAdapter);

    // 设置右侧触摸监听
    mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
      @Override public void onTouchingLetterChanged(String s) {
        if ("热".equals(s)) {
          mCityLv.smoothScrollToPositionFromTop(0, -titleHeight, 0);
        } else if ("#".equals(s)) {
          int offset = BeeUtils.getDimenPixels(R.dimen.px_60dp);
          mCityLv.smoothScrollToPositionFromTop(1, offset, 0);
        }
        // 该字母首次出现的位置
        int position = mAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
          //因为有headView,所以实际的位置要加1.
          mCityLv.setSelection(position + 1);
        }
      }
    });
  }

  private void setHeaderView() {
    View mHeaderView =
        LayoutInflater.from(getActivity()).inflate(R.layout.fragment_city_header, null);
    currentTv = (TextView) mHeaderView.findViewById(R.id.tv_city_current);
    resetCityName();
    locationTv = (TextView) mHeaderView.findViewById(R.id.tv_location_city);
    showLocation();
    mHeaderView.findViewById(R.id.tv_all_city).setOnClickListener(this);
    BeeGridView gv = (BeeGridView) mHeaderView.findViewById(R.id.gv_city_hot);
    int resId = R.layout.gvitem_city;
    gvAdapter = new CityGridViewAdapter(getActivity(), mHotCityData, resId);
    gvAdapter.setListener(this);
    gv.setAdapter(gvAdapter);
    mCityLv.addHeaderView(mHeaderView);
  }

  private void resetCityName() {
    currentTv.setText(
        BeeUtils.getResString(R.string.city_current_choose, SpUtils.getCurrentCityName()));
  }

  private List<CityEntity> sortCity(List<CityEntity> cityEntities) {
    Collections.sort(cityEntities, new ComparatorCity());
    return cityEntities;
  }

  private void showLocation() {
    if (getActivity() instanceof MainActivity) {
      MainActivity activity = (MainActivity) getActivity();
      BeeService.BeeServiceBinder binder = activity.getServiceBinder();
      if (binder != null) {
        LocationEntity location = binder.getBaiduLocation();
        if (location != null) {
          String city_name = location.getCity_name();
          if (!TextUtils.isEmpty(city_name)) {
            CityEntity mLocCityEntity = SpUtils.queryByCityName(city_name);
            if (mLocCityEntity != null) {
              locationTv.setText(mLocCityEntity.getCity_name());
              locationTv.setTag(mLocCityEntity);
              locationTv.setClickable(true);
              locationTv.setOnClickListener(this);
            } else {
              locationTv.setText(BeeUtils.getResString(R.string.city_location_dredge, city_name));
              locationTv.setClickable(false);
            }
          }
        }
      }
    }
  }
}
