package com.haoche51.bee.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.BindView;
import com.haoche51.bee.R;
import com.haoche51.bee.adapter.HomeBrandGridViewAdapter;
import com.haoche51.bee.custom.PlatformImageView;
import com.haoche51.bee.custom.PullToRefresh;
import com.haoche51.bee.entity.EventBusEntity;
import com.haoche51.bee.entity.HomeDataEntity;
import com.haoche51.bee.net.API;
import com.haoche51.bee.net.GsonParse;
import com.haoche51.bee.net.HttpCallBack;
import com.haoche51.bee.net.HttpTask;
import com.haoche51.bee.net.ParamsUtil;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.SpUtils;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 显示首页的Fragment
 */
public class HomePageFragment extends BeeFragment
    implements View.OnClickListener, RadioGroup.OnCheckedChangeListener,
    PlatformImageView.ActionListener {

  @BindView(R.id.ptr_home_list) PullToRefresh mPullToRefresh;

  private final int width = BeeUtils.getScreenWidthInPixels();

  private View mHeaderView;

  /** 顶部图片模块 */
  private TextView mTopCityTv;
  private TextView mTopCountTv;

  /** 搜索模块 */
  private TextView mSearchCityTv;

  /** 快速选车模块 */
  private GridView mChooseBrandGv;

  /** 今日新上模块 */
  private TextView mTodayTv;

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_home_page;
  }

  @Override void doInitViewOrData() {

    int resLayout = R.layout.fragment_home_page_headerview;
    mHeaderView = LayoutInflater.from(getActivity()).inflate(resLayout, null);

    initListView();

    initViews();

    setUpHotPrice();

    requestData();

    HttpTask.updateCity();
  }

  private void initListView() {
    ListView mListView = mPullToRefresh.getListView();
    mListView.setVerticalScrollBarEnabled(false);
    mListView.setDividerHeight(0);
    mListView.addHeaderView(mHeaderView);

    int layout = android.R.layout.simple_list_item_1;
    ArrayAdapter<String> mAdapter = new ArrayAdapter<>(getActivity(), layout);
    mListView.setAdapter(mAdapter);

    mPullToRefresh.setNeedDetectXY(true);
    mPullToRefresh.setNoDefaultDivider();
    mPullToRefresh.removeFooter();

    mPullToRefresh.setOnRefreshCallback(new PullToRefresh.OnRefreshCallback() {
      @Override public void onPullDownRefresh() {
        if (!BeeUtils.isNetAvailable()) {
          mPullToRefresh.finishRefresh();
          BeeUtils.showToast(R.string.bee_net_unreachable);
          return;
        }
        requestData();
      }

      @Override public void onLoadMoreRefresh() {
      }
    });
  }

  private void initViews() {

    /** 顶部图片模块 */
    FrameLayout mTopLayout = (FrameLayout) mHeaderView.findViewById(R.id.frame_home_top);
    mTopLayout.getLayoutParams().height = (int) (width * 400 / 750F);
    mTopCityTv = (TextView) mHeaderView.findViewById(R.id.tv_home_top_city);
    mTopCityTv.setText(SpUtils.getCurrentCityName());
    mTopCountTv = (TextView) mHeaderView.findViewById(R.id.tv_home_top_count);
    /** 搜索模块 */
    mSearchCityTv = (TextView) mHeaderView.findViewById(R.id.tv_home_search_city);
    mSearchCityTv.setText(SpUtils.getCurrentCityName());
    mHeaderView.findViewById(R.id.frame_home_search_city).setOnClickListener(this);
    mHeaderView.findViewById(R.id.frame_home_search).setOnClickListener(this);
    /** 快速选车模块 */
    mChooseBrandGv = (GridView) mHeaderView.findViewById(R.id.gv_home_brand);
    /** 今日新上模块 */
    FrameLayout mTodayLayout = (FrameLayout) mHeaderView.findViewById(R.id.frame_home_today);
    mTodayLayout.getLayoutParams().height = (int) (width * 211 / 750F);
    mTodayTv = (TextView) mHeaderView.findViewById(R.id.tv_home_today_count);
    /** 平台简介 */
    PlatformImageView mPlatformIv = (PlatformImageView) mHeaderView.findViewById(R.id.piv_home);
    mPlatformIv.getLayoutParams().height = (int) (width * 656 / 750F);
    mPlatformIv.setOnActionListener(this);
  }

  @Override public void actionListener(int actionIndex) {
    BeeEvent.postEvent(BeeEvent.ACTION_SHOW_PLATFORM_FRAGMENT, actionIndex);
    BeeStatistics.homePageClick(BeeUtils.getResArray(R.array.platform_name_list)[actionIndex]);
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.frame_home_search_city:
        BeeEvent.postEvent(BeeEvent.ACTION_SHOW_CITY_FRAGMENT);
        BeeStatistics.homePageClick("城市切换");
        break;
      case R.id.frame_home_search:
        BeeEvent.postEvent(BeeEvent.ACTION_GO_SEARCH);
        BeeStatistics.homePageClick("搜索");
        break;
    }
  }

  /** 热门价格 */
  private void setUpHotPrice() {
    RadioGroup rp0 = (RadioGroup) mHeaderView.findViewById(R.id.hot_rg_0);
    RadioGroup rp1 = (RadioGroup) mHeaderView.findViewById(R.id.hot_rg_1);
    rp0.setOnCheckedChangeListener(this);
    rp1.setOnCheckedChangeListener(this);
  }

  @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
    if (isCurrentActivityValid()) {
      handleHotPrice(checkedId);
      // 为了让每一次都响应点击
      ((RadioButton) group.findViewById(checkedId)).setChecked(false);
    }
  }

  private void handleHotPrice(int checkedId) {
    int[] ids = {
        R.id.rb_price_0, R.id.rb_price_1, R.id.rb_price_2, R.id.rb_price_3, R.id.rb_price_4,
        R.id.rb_price_5, R.id.rb_price_6, R.id.rb_price_7
    };

    String[] priceArr = BeeUtils.getResArray(R.array.home_hot_price);
    int index = 0;
    for (; index < ids.length; index++) {
      if (ids[index] == checkedId) {
        break;
      }
    }
    FilterUtils.resetFilterTerm(FilterUtils.ALL);
    FilterUtils.priceKey2FilterTerm(FilterUtils.ALL, priceArr[index]);
    BeeEvent.postEvent(BeeEvent.ACTION_CHANGE_MAIN_TAB);
    BeeEvent.postEvent(FilterUtils.ALL + BeeEvent.ACTION_PRICE_CHOOSE_CHANGE);
    BeeStatistics.homePageClick(priceArr[index]);
  }

  private void requestData() {
    Map<String, Object> cityParams = ParamsUtil.getHomeCityData();
    API.post(cityParams, new HttpCallBack() {
      @Override public void onFinish(String responseJsonString) {
        if (!TextUtils.isEmpty(responseJsonString)) {
          handleCityData(responseJsonString);
        }
      }
    });
  }

  private void handleCityData(String resp) {

    HomeDataEntity entity = GsonParse.parseHomeCityData(resp);

    //品牌
    List<Integer> brands = entity.getHot_brand();
    if (!BeeUtils.isListEmpty(brands) && brands.size() == 3) {
      setUpBrand(brands);
    }
    mTopCountTv.setText(BeeUtils.getResString(R.string.bee_number, entity.getCity_count()));
    mTodayTv.setText(BeeUtils.getResString(R.string.bee_number, entity.getToday_count()));

    mPullToRefresh.finishRefresh();
  }

  private void setUpBrand(List<Integer> brands) {
    if (!isCurrentActivityValid()) return;

    //因为只返回三个数据,所以要加一个任意数据凑4个。
    brands.add(0);
    int brandRes = R.layout.gvitem_home_brand;
    HomeBrandGridViewAdapter gvAdapter =
        new HomeBrandGridViewAdapter(getActivity(), brands, brandRes);
    mChooseBrandGv.setAdapter(gvAdapter);
  }

  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN)
  public void cityEvent(EventBusEntity entity) {
    if (entity.getAction().equals(BeeEvent.ACTION_CITY_CHANGE)) {
      mTopCityTv.setText(entity.getStrValue());
      mSearchCityTv.setText(entity.getStrValue());
      mPullToRefresh.autoRefresh();
    }
  }
}