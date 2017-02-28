package com.haoche51.bee.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import com.haoche51.bee.R;
import com.haoche51.bee.adapter.FilterSeriesAdapter;
import com.haoche51.bee.dao.FilterTerm;
import com.haoche51.bee.entity.SeriesEntity;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.FilterUtils;
import java.util.ArrayList;
import java.util.List;

public class FilterCarSeriesFragment extends BeeFragment {

  @BindView(R.id.iv_filter_series_back) View mBackIv;
  @BindView(R.id.tv_filter_series_top_title) TextView mTopTitle;
  @BindView(R.id.v_filter_series_top_line) View mTopLine;
  @BindView(R.id.lv_car_series) ListView mContentLv;

  private View mHeaderView;
  private RelativeLayout mAllSeries;
  private TextView mUnLimitTv;
  private ImageView mUnLimitIv;

  private int titleHeight = BeeUtils.getDimenPixels(R.dimen.px_64dp);

  private SeriesEntity unlimited = new SeriesEntity(-1, -1);

  private FilterSeriesAdapter mAdapter;
  private List<SeriesEntity> mSeriesData = new ArrayList<>();

  private int type;

  public void setType(int type) {
    this.type = type;
  }

  public static FilterCarSeriesFragment newInstance() {
    return new FilterCarSeriesFragment();
  }

  @Override boolean isNeedBindEventBus() {
    return false;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_filter_car_series;
  }

  @Override void doInitViewOrData() {
    if (mContentLv == null) return;

    mBackIv.setOnClickListener(mClickListener);
    setHeaderView();

    int itemRes = R.layout.lvitem_filter_car_series;
    mAdapter = new FilterSeriesAdapter(getActivity(), mSeriesData, itemRes, type, mClickListener);
    mContentLv.setAdapter(mAdapter);

    mContentLv.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override public void onScrollStateChanged(AbsListView view, int scrollState) {

      }

      @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        if (titleHeight + mHeaderView.getTop() > 0) {
          mTopTitle.setVisibility(View.GONE);
          mTopLine.setVisibility(View.GONE);
        } else {
          mTopTitle.setVisibility(View.VISIBLE);
          mTopLine.setVisibility(View.VISIBLE);
        }
      }
    });
  }

  private void setHeaderView() {
    mHeaderView =
        LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_series_header, null);
    mAllSeries = (RelativeLayout) mHeaderView.findViewById(R.id.rel_all_series);
    mUnLimitTv = (TextView) mHeaderView.findViewById(R.id.tv_all_series);
    mUnLimitIv = (ImageView) mHeaderView.findViewById(R.id.iv_all_series_choose);
    mAllSeries.setOnClickListener(mClickListener);
    mContentLv.addHeaderView(mHeaderView);
  }

  public void setCarSeries(int brand_id, String brand_name, List<SeriesEntity> seriesData) {
    if (mBackIv == null) return;
    this.mSeriesData.clear();
    unlimited.setBrand_id(brand_id);
    unlimited.setBrand_name(brand_name);
    this.mSeriesData.addAll(seriesData);
    mAdapter.notifyDataSetChanged();
    mAllSeries.setTag(unlimited);
    FilterTerm term = FilterUtils.getFilterTerm(type);
    if (term.getBrand_id() == brand_id && term.getClass_id() <= 0) {
      mUnLimitTv.setTextColor(BeeUtils.getResColor(R.color.common_yellow));
      mUnLimitIv.setVisibility(View.VISIBLE);
    }
  }

  private View.OnClickListener mClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      SeriesEntity entity = null;

      switch (v.getId()) {
        case R.id.iv_filter_series_back:
          break;
        case R.id.tv_car_series_item:
        case R.id.rel_all_series:
          Object o = v.getTag();
          if (o != null && o instanceof SeriesEntity) {
            entity = (SeriesEntity) o;
          }
          break;
      }
      BeeEvent.postEvent(type + BeeEvent.ACTION_CAR_SERIES_CHOOSE, entity);
    }
  };
}
