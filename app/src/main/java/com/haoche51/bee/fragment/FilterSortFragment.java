package com.haoche51.bee.fragment;

import android.widget.ListView;
import butterknife.BindView;
import butterknife.OnClick;
import com.haoche51.bee.R;
import com.haoche51.bee.adapter.FilterSortAdapter;
import com.haoche51.bee.entity.KeyValueEntity;
import com.haoche51.bee.util.BeeConstants;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.FilterUtils;
import java.util.List;

public class FilterSortFragment extends BeeFragment {

  @BindView(R.id.lv_filter_sort_main) ListView mPriceLv;

  private int type;

  public void setType(int type) {
    this.type = type;
  }

  public static FilterSortFragment newInstance() {
    return new FilterSortFragment();
  }

  @Override boolean isNeedBindEventBus() {
    return false;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_filter_sort;
  }

  @Override void doInitViewOrData() {
    List<KeyValueEntity> data = FilterUtils.getDefaultSortData(BeeConstants.FILTER_SORT);
    int layoutId = R.layout.lvitem_sort;
    FilterSortAdapter adapter = new FilterSortAdapter(getActivity(), data, layoutId, type);
    mPriceLv.setAdapter(adapter);
  }

  @OnClick(R.id.iv_filter_sort_close) public void close() {
    BeeEvent.postEvent(BeeEvent.ACTION_HIDE_SORT_FRAGMENT);
    BeeStatistics.closeClick("排序");
  }

  @OnClick(R.id.linear_sort_parent) public void setNull() {
  }
}
