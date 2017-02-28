package com.haoche51.bee.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.haoche51.bee.R;
import com.haoche51.bee.adapter.SinglePicVehicleAdapter;
import com.haoche51.bee.custom.PullToRefresh;
import com.haoche51.bee.dao.FilterTerm;
import com.haoche51.bee.entity.BVehicleItemEntity;
import com.haoche51.bee.entity.EventBusEntity;
import com.haoche51.bee.entity.FilterSearchEntity;
import com.haoche51.bee.entity.BFilterSearchEntity;
import com.haoche51.bee.entity.VehicleItemEntity;
import com.haoche51.bee.net.API;
import com.haoche51.bee.net.GsonParse;
import com.haoche51.bee.net.HttpCallBack;
import com.haoche51.bee.net.ParamsUtil;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeConstants;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.SpUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VehicleListFragment extends BeeFragment {

  @BindView(R.id.tv_filter_bar_sort) TextView mSortTv;
  @BindView(R.id.tv_filter_bar_brand) TextView mBrandTv;
  @BindView(R.id.tv_filter_bar_price) TextView mPriceTv;
  @BindView(R.id.tv_filter_bar_detail) TextView mMoreTv;
  @BindView(R.id.ptr_buy_list) PullToRefresh mPullToRefresh;

  @BindView(R.id.linear_net_refresh) LinearLayout mNetLayout;

  @BindView(R.id.tv_city_choose) TextView mCityTv;

  private RelativeLayout mConditionParent;
  private LinearLayout mLinearCondition;

  private View mEmptyView;

  private List<VehicleItemEntity> mVehicleData = new ArrayList<>();
  private SinglePicVehicleAdapter mAdapter;
  private int mCurrentPage = 0;

  /** 标记如果为排序引起的变化,不弹出提示 */
  private boolean isSortChangedData = false;

  /** 记录上一次筛选条件 */
  private FilterTerm mLastTerm;

  /** 记录上一次搜索词 */
  private String mLastSearchWord = "";

  /** 标识是否为搜索结果返回 */
  private boolean isSearchResult = false;

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_vehicle_list;
  }

  @Override void doInitViewOrData() {
    initListView();
    initPullToRefresh();
    initRefreshListener();
  }

  @OnClick(R.id.tv_city_choose) public void showCityChoose() {
    BeeEvent.postEvent(BeeEvent.ACTION_SHOW_CITY_FRAGMENT);
    BeeStatistics.vehicleListClick("城市切换");
  }

  @OnClick(R.id.tv_search_choose) public void goSearch() {
    BeeEvent.postEvent(BeeEvent.ACTION_GO_SEARCH);
    BeeStatistics.vehicleListClick("搜索");
  }

  @OnClick({
      R.id.rel_filter_bar_sort, R.id.rel_filter_bar_brand, R.id.rel_filter_bar_price,
      R.id.rel_filter_bar_detail
  }) public void OnFilterBarClick(View v) {
    handleFilterClick(v);
  }

  private void handleFilterClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.rel_filter_bar_brand:// 筛选栏_品牌
        BeeEvent.postEvent(BeeEvent.ACTION_SHOW_BRAND_FRAGMENT, FilterUtils.ALL);
        BeeStatistics.vehicleListClick("品牌");
        break;
      case R.id.rel_filter_bar_sort:// 筛选栏_排序
        BeeEvent.postEvent(BeeEvent.ACTION_SHOW_SORT_FRAGMENT, FilterUtils.ALL);
        BeeStatistics.vehicleListClick("排序");
        break;
      case R.id.rel_filter_bar_price:// 筛选栏_价格
        BeeEvent.postEvent(BeeEvent.ACTION_SHOW_PRICE_FRAGMENT, FilterUtils.ALL);
        BeeStatistics.vehicleListClick("价格");
        break;
      case R.id.rel_filter_bar_detail:// 筛选栏_细节
        BeeEvent.postEvent(BeeEvent.ACTION_SHOW_MORE_FRAGMENT, FilterUtils.ALL);
        BeeStatistics.vehicleListClick("更多");
        break;
    }
  }

  private void initListView() {
    int headerRes = R.layout.fragment_vehicle_list_condition;
    View mHeaderView = LayoutInflater.from(getActivity()).inflate(headerRes, null);
    ListView mListView = mPullToRefresh.getListView();
    mListView.addHeaderView(mHeaderView);

    int emptyRes = R.layout.empty_view_for_vehicle_list;
    View v = LayoutInflater.from(getActivity()).inflate(emptyRes, null);
    mEmptyView = v.findViewById(R.id.linear_empty_view);
    mListView.addHeaderView(v);

    mCityTv.setText(SpUtils.getCurrentCityName());
    mConditionParent = (RelativeLayout) mHeaderView.findViewById(R.id.rel_choose_parent);
    mLinearCondition = (LinearLayout) mHeaderView.findViewById(R.id.linear_filter_choose);
  }

  private void initPullToRefresh() {
    int itemRes = R.layout.lvitem_vehicle_list;
    mAdapter = new SinglePicVehicleAdapter(getActivity(), mVehicleData, itemRes);
    mPullToRefresh.setAdapter(mAdapter);
    mAdapter.notifyDataSetChanged();
    mPullToRefresh.setNeedDetectXY(true);
    mPullToRefresh.setFirstAutoRefresh();
    mPullToRefresh.setNoDefaultDivider();
  }

  private void initRefreshListener() {

    mPullToRefresh.setOnRefreshCallback(new PullToRefresh.OnRefreshCallback() {
      @Override public void onPullDownRefresh() {
        mCurrentPage = 0;
        requestData();
      }

      @Override public void onLoadMoreRefresh() {
        mCurrentPage++;
        requestData();
      }
    });
  }

  private void requestData() {
    if (isSearchResult) {
      requestSearchData();
    } else {
      requestVehicleSource();
    }
    mLastTerm = FilterUtils.getFilterTerm(FilterUtils.ALL);
  }

  private void requestSearchData() {
    if (TextUtils.isEmpty(mLastSearchWord)) return;

    final Map<String, Object> params = ParamsUtil.getSearchResult(mLastSearchWord);
    API.post(params, new HttpCallBack() {
      @Override public void onFinish(String response) {
        handleSearchData(response);
      }
    });
  }

  private void handleSearchData(String response) {

    if (mPullToRefresh == null) return;
    if (TextUtils.isEmpty(response)) {
      mPullToRefresh.finishRefresh();
      mNetLayout.setVisibility(View.VISIBLE);
      return;
    }
    mNetLayout.setVisibility(View.GONE);
    BFilterSearchEntity entity = GsonParse.parseSearchResult(response);
    if (entity != null) {
      FilterSearchEntity queryEntity = entity.getData();
      FilterUtils.saveQueryToFilterTerm(queryEntity);
      if (queryEntity == null || queryEntity.equals(new FilterSearchEntity())) {
        seeEmptyView(null);
        mPullToRefresh.finishRefresh();
        showSearchView();
      } else {
        requestVehicleSource();
      }
    }
  }

  private void requestVehicleSource() {
    Map<String, Object> params = ParamsUtil.getVehicleSourceList(mCurrentPage);
    API.post(params, new HttpCallBack() {
      @Override public void onFinish(String response) {
        handleVehicleSource(response);
      }
    });
    mLastTerm = FilterUtils.getFilterTerm(FilterUtils.ALL);
  }

  private void handleVehicleSource(String responseJsonString) {

    if (mPullToRefresh == null) return;
    if (seeIfResponseEmpty(responseJsonString)) return;

    mNetLayout.setVisibility(View.GONE);
    mEmptyView.setVisibility(View.GONE);
    mPullToRefresh.showFooter();
    showConditionView();

    try {
      BVehicleItemEntity entity = GsonParse.parseGetVehicleSourceList(responseJsonString);
      int count = BeeUtils.str2Int(entity.getCount());
      seeIfNeedScrollUp(count);

      List<VehicleItemEntity> datas = entity.getVehicles();
      seeEmptyView(datas);
      if (!BeeUtils.isListEmpty(datas)) {
        boolean isNoMoreData = datas.size() < BeeConstants.PAGE_SIZE;
        mVehicleData.addAll(datas);
        mPullToRefresh.setFooterStatus(isNoMoreData);
        mAdapter.notifyDataSetChanged();
      }
    } catch (Exception e) {
      //
    }
    mPullToRefresh.finishRefresh();
  }

  private void seeEmptyView(List<VehicleItemEntity> datas) {
    if (mCurrentPage == 0 && BeeUtils.isListEmpty(datas)) {
      mVehicleData.clear();
      mAdapter.notifyDataSetChanged();
      mEmptyView.setVisibility(View.VISIBLE);
      mPullToRefresh.hideFooter();
    }
  }

  private boolean seeIfResponseEmpty(String responseJsonString) {
    boolean result = false;
    if (TextUtils.isEmpty(responseJsonString)) {
      result = true;
      mPullToRefresh.setFooterStatus(false);
      mPullToRefresh.finishRefresh();
      if (mVehicleData.isEmpty()) {
        mNetLayout.setVisibility(View.VISIBLE);
      } else {
        BeeUtils.toastNetError();
      }
    }
    return result;
  }

  private void seeIfNeedScrollUp(int count) {
    if (mCurrentPage == 0) {
      mVehicleData.clear();
      mPullToRefresh.postDelayed(new Runnable() {
        @Override public void run() {
          if (mPullToRefresh != null) {
            mPullToRefresh.tryToSmoothScrollUp();
          }
        }
      }, 10);

      if (!isSortChangedData && count > 0 && isVisible()) {
        BeeUtils.showCountToast(count);
      }
    }
  }

  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN)
  public void filterChangeEvent(EventBusEntity entity) {
    String action = entity.getAction();
    switch (action) {
      case FilterUtils.ALL + BeeEvent.ACTION_SORT_CHOOSE:
        isSearchResult = false;
        isSortChangedData = true;
        onFilterChanged();
        break;
      case FilterUtils.ALL + BeeEvent.ACTION_FILTER_CHANGE:
        isSearchResult = false;
        isSortChangedData = false;
        onFilterChanged();
        break;
      case BeeEvent.ACTION_SEND_KEY_WORD://搜索返回
        isSearchResult = true;
        isSortChangedData = false;
        handleSearchResult(entity);
        break;
      case BeeEvent.ACTION_REFRESH_VEHICLE_LIST://首页跳转
        isSearchResult = false;
        isSortChangedData = false;
        mPullToRefresh.autoRefresh();
        setFilterBarColor();
        break;
      case BeeEvent.ACTION_CITY_CHANGE:
        mCityTv.setText(entity.getStrValue());
        isSearchResult = false;
        isSortChangedData = false;
        mPullToRefresh.autoRefresh();
        break;
    }
  }

  private void handleSearchResult(EventBusEntity entity) {

    if (mPullToRefresh == null) return;
    mLastSearchWord = entity.getStrValue();
    mPullToRefresh.autoRefresh();
  }

  private void onFilterChanged() {
    if (mPullToRefresh == null) return;

    boolean isFilterChanged = !FilterUtils.getFilterTerm(FilterUtils.ALL).equals(mLastTerm);
    if (isFilterChanged) {
      mPullToRefresh.autoRefresh();
    }
    setFilterBarColor();
  }

  public void setFilterBarColor() {
    if (mBrandTv == null) return;
    FilterTerm term = FilterUtils.getFilterTerm(FilterUtils.ALL);

    int chooseColor = BeeUtils.getResColor(R.color.common_yellow);
    int normalColor = BeeUtils.getResColor(R.color.common_text_black);
    int brandColor = normalColor;

    if (term.getBrand_id() > 0) {
      brandColor = chooseColor;
    }
    mBrandTv.setTextColor(brandColor);

    int sortColor = "智能排序".equals(term.getDescriptionSort()) ? normalColor : chooseColor;
    mSortTv.setTextColor(sortColor);

    int priceColor =
        (term.getLowPrice() == 0F && term.getHighPrice() == 0F) ? normalColor : chooseColor;
    mPriceTv.setTextColor(priceColor);

    int moreColor = FilterUtils.isMoreDefault(term) ? normalColor : chooseColor;
    mMoreTv.setTextColor(moreColor);
  }

  /** 把搜索的关键词展示出来 */
  private void showSearchView() {

    if (mConditionParent == null) return;

    mLinearCondition.removeAllViews();

    FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity())
        .inflate(R.layout.item_condition, mLinearCondition, false);
    TextView textView = (TextView) (layout.findViewById(R.id.tv_filter_choose_item));
    textView.setText(mLastSearchWord);
    textView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        isSearchResult = false;
        mPullToRefresh.autoRefresh();
      }
    });
    mLinearCondition.addView(layout);

    if (mLinearCondition.getChildCount() != 0) {
      mConditionParent.setVisibility(View.VISIBLE);
    } else {
      mConditionParent.setVisibility(View.GONE);
    }
  }

  /** 把筛选条件展示出来 */
  private void showConditionView() {

    if (mConditionParent == null) return;

    if (FilterUtils.isCurrentDefaultCondition()) {
      mConditionParent.setVisibility(View.GONE);
    } else {
      mLinearCondition.removeAllViews();

      //除去平台外的筛选条件
      TreeMap<Integer, String> map = FilterUtils.getConditions();
      for (Map.Entry<Integer, String> entry : map.entrySet()) {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity())
            .inflate(R.layout.item_condition, mLinearCondition, false);
        TextView textView = (TextView) (layout.findViewById(R.id.tv_filter_choose_item));
        textView.setText(entry.getValue());
        textView.setTag(R.id.tv_filter_choose_item, entry.getKey());
        textView.setOnClickListener(mDeleteConditionListener);
        mLinearCondition.addView(layout);
      }

      //平台的筛选条件
      List<String> platform = FilterUtils.getFilterTerm(FilterUtils.ALL).getPlatform();
      if (!BeeUtils.isListEmpty(platform)) {
        for (int i = 0; i < platform.size(); i++) {
          FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity())
              .inflate(R.layout.item_condition, mLinearCondition, false);
          TextView textView = (TextView) (layout.findViewById(R.id.tv_filter_choose_item));
          textView.setText(platform.get(i));
          textView.setTag(R.id.tv_filter_choose_item, BeeConstants.FILTER_PLATFORM);
          textView.setOnClickListener(mDeleteConditionListener);
          mLinearCondition.addView(layout);
        }
      }
      if (mLinearCondition.getChildCount() != 0) {
        mConditionParent.setVisibility(View.VISIBLE);
      } else {
        mConditionParent.setVisibility(View.GONE);
      }
    }
  }

  private View.OnClickListener mDeleteConditionListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      handleDeleteCondition(v);
    }
  };

  private void handleDeleteCondition(View v) {

    FilterTerm term = FilterUtils.getFilterTerm(FilterUtils.ALL);

    int curViewTag = (int) v.getTag(R.id.tv_filter_choose_item);

    switch (curViewTag) {
      case BeeConstants.FILTER_BRAND:
        term.setBrand_id(0);
        term.setClass_id(0);
        break;
      case BeeConstants.FILTER_SERIES:
        term.setClass_id(0);
        break;
      case BeeConstants.FILTER_PRICE:
        term.setHighPrice(0);
        term.setLowPrice(0);
        break;
      case BeeConstants.FILTER_CAR_AGE:
        term.setFrom_year(0);
        term.setTo_year(0);
        BeeEvent.postEvent(FilterUtils.ALL + BeeEvent.ACTION_MORE_CAR_AGE_CHOOSE_CHANGE);
        break;
      case BeeConstants.FILTER_DISTANCE:
        term.setFrom_miles(0);
        term.setTo_miles(0);
        BeeEvent.postEvent(FilterUtils.ALL + BeeEvent.ACTION_MORE_DISTANCE_CHOOSE_CHANGE);
        break;
      case BeeConstants.FILTER_SPEED_BOX:
        term.setGearboxType(0);
        break;
      case BeeConstants.FILTER_CAR_TYPE:
        term.setStructure(0);
        break;
      case BeeConstants.FILTER_PLATFORM:
        if (v instanceof TextView) {
          String text = ((TextView) v).getText().toString();
          List<String> list = term.getPlatform();
          if (list == null) list = new ArrayList<>();
          if (list.contains(text)) {
            list.remove(text);
          }
          term.setPlatform(list);
        }
        break;
    }

    isSearchResult = false;

    FilterUtils.setFilterTerm(term, FilterUtils.ALL);

    BeeEvent.postEvent(FilterUtils.ALL + BeeEvent.ACTION_BRAND_CHOOSE_CHANGE);
    BeeEvent.postEvent(FilterUtils.ALL + BeeEvent.ACTION_PRICE_CHOOSE_CHANGE);
    BeeEvent.postEvent(FilterUtils.ALL + BeeEvent.ACTION_MORE_CHOOSE_CHANGE);

    setFilterBarColor();
    mPullToRefresh.autoRefresh();
  }
}
