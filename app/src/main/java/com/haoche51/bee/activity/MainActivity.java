package com.haoche51.bee.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import butterknife.BindView;
import com.haoche51.bee.BeeService;
import com.haoche51.bee.R;
import com.haoche51.bee.dao.SeriesDAO;
import com.haoche51.bee.entity.BrandEntity;
import com.haoche51.bee.entity.EventBusEntity;
import com.haoche51.bee.entity.SeriesEntity;
import com.haoche51.bee.fragment.CityFragment;
import com.haoche51.bee.fragment.FilterBrandFragment;
import com.haoche51.bee.fragment.FilterCarSeriesFragment;
import com.haoche51.bee.fragment.FilterMoreFragment;
import com.haoche51.bee.fragment.FilterPriceFragment;
import com.haoche51.bee.fragment.FilterSortFragment;
import com.haoche51.bee.fragment.PlatformFragment;
import com.haoche51.bee.net.API;
import com.haoche51.bee.net.GsonParse;
import com.haoche51.bee.net.HttpCallBack;
import com.haoche51.bee.net.ParamsUtil;
import com.haoche51.bee.util.BeeConstants;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.DbUtils;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.FragmentController;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.SpUtils;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BeeActivity {

  @BindView(R.id.navigation_bar) RadioGroup mNavigatorRp;

  /** 标识当前Activity是否存活 */
  private boolean isActive = true;
  /** 是否是第一次加载首页 */
  private boolean isFirstLoadHomePage = true;
  /** 记录上一次fragment的tag */
  private String mLastFragmentTag = "";

  @Override void initViews() {
    BeeEvent.register(this);
    mNavigatorRp.setOnCheckedChangeListener(mOnCheckListener);
    bindPollService();

    BeeUtils.do360Update();

    FilterUtils.resetFilterTerm(FilterUtils.ALL);
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    hideTitleBar();
  }

  @Override int getContentViewResourceId() {
    return R.layout.activity_main;
  }

  private OnCheckedChangeListener mOnCheckListener = new OnCheckedChangeListener() {
    @Override public void onCheckedChanged(RadioGroup group, int checkedId) {

      switch (checkedId) {
        case R.id.rb_tab_home_page:// 首页
          changeFragmentByTag(FragmentController.HOME_TAG);
          BeeStatistics.navigationBarClick("首页");
          break;
        case R.id.rb_tab_core_vehicle:// 买车
          changeFragmentByTag(FragmentController.CHOOSE_TAG);
          BeeStatistics.navigationBarClick("买车");
          break;
        case R.id.rb_tab_profile:// 我的
          changeFragmentByTag(FragmentController.PROFILE_TAG);
          BeeStatistics.navigationBarClick("我的");
          break;
      }
    }
  };

  private void changeFragmentByTag(String fragmentTag) {
    if (!isActive) return;

    if (fragmentTag.equals(mLastFragmentTag)) {
      return;
    }
    try {
      FragmentManager mFragmentManager = getSupportFragmentManager();
      FragmentTransaction mTransaction = mFragmentManager.beginTransaction();

      if (mFragmentManager.findFragmentByTag(fragmentTag) != null) {
        Fragment currentFragment = mFragmentManager.findFragmentByTag(fragmentTag);
        mTransaction.show(currentFragment);
      } else {
        Fragment currentFragment = FragmentController.newInstance(fragmentTag);
        mTransaction.add(R.id.fragment_container, currentFragment, fragmentTag);
      }

      Fragment lastFragment = mFragmentManager.findFragmentByTag(mLastFragmentTag);
      if (lastFragment != null) {
        mTransaction.hide(lastFragment);
      }
      mTransaction.commitAllowingStateLoss();
      mFragmentManager.executePendingTransactions();

      mLastFragmentTag = fragmentTag;
    } catch (Exception e) {
      //
    }
  }

  private long mLastKeyBackTime;

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    long now = System.currentTimeMillis();
    boolean isExit = now - mLastKeyBackTime <= 2000;
    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && !isExit) {
      mLastKeyBackTime = now;
      BeeUtils.showToast(R.string.bee_back_again_exit);
    }
    return !isExit || super.onKeyDown(keyCode, event);
  }

  @Override protected void onResume() {
    super.onResume();
    isActive = true;
    if (isFirstLoadHomePage) {
      mNavigatorRp.check(R.id.rb_tab_home_page);
      isFirstLoadHomePage = false;
    }
  }

  @Override protected void onDestroy() {
    isActive = false;
    doRelease();
    super.onDestroy();
    BeeEvent.unRegister(this);
  }

  private void doRelease() {
    if (mServiceConnection != null) {
      unbindService(mServiceConnection);
    }
    if (mServiceBinder != null) {
      mServiceBinder.getPollService().stopSelf();
    }
  }

  /** 绑定Service */
  private ServiceConnection mServiceConnection;
  private BeeService.BeeServiceBinder mServiceBinder;

  public BeeService.BeeServiceBinder getServiceBinder() {
    return mServiceBinder;
  }

  private void bindPollService() {
    if (mServiceBinder == null) {
      Intent service = new Intent(this, BeeService.class);
      mServiceConnection = new ServiceConnection() {
        @Override public void onServiceDisconnected(ComponentName name) {
        }

        @Override public void onServiceConnected(ComponentName name, IBinder service) {
          if (service != null && service instanceof BeeService.BeeServiceBinder) {
            mServiceBinder = (BeeService.BeeServiceBinder) service;
          }
        }
      };
      bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
  }

  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN)
  public void homeReturnEvent(EventBusEntity entity) {
    if (entity.getAction().equals(BeeEvent.ACTION_CHANGE_MAIN_TAB)) {
      mNavigatorRp.check(R.id.rb_tab_core_vehicle);
      BeeEvent.postEvent(BeeEvent.ACTION_REFRESH_VEHICLE_LIST);
    }
  }

  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN)
  public void searchEvent(EventBusEntity entity) {
    if (entity.getAction().equals(BeeEvent.ACTION_GO_SEARCH)) {
      Intent intent = new Intent(this, SearchActivity.class);
      startActivityForResult(intent, BeeConstants.REQUEST_CODE_FOR_SEARCH);
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == BeeConstants.REQUEST_CODE_FOR_SEARCH) {
      if (data != null && data.hasExtra(BeeConstants.KEY_FOR_SEARCH_KEY) && mNavigatorRp != null) {
        mNavigatorRp.check(R.id.rb_tab_core_vehicle);
        String keyword = data.getStringExtra(BeeConstants.KEY_FOR_SEARCH_KEY);
        //将关键词传递到车辆列表页面
        BeeEvent.postEvent(BeeEvent.ACTION_SEND_KEY_WORD, keyword);
      }
    }
  }

  /**  ======================== 以下是筛选栏相关的东西 ==============================  */
  /** 标示是否正在请求数据 */
  private boolean isRequestBrandData = false;

  private String BRAND_TAG;
  private String SERIES_TAG;
  private String SORT_TAG;
  private String PRICE_TAG;
  private String MORE_TAG;

  private int mFrameLayoutId = R.id.frame_core_filter;
  private int type;

  private void initTags(int type) {
    if (type == 0) return;
    this.type = type;
    BRAND_TAG = type + FilterBrandFragment.class.getName();
    SERIES_TAG = type + FilterCarSeriesFragment.class.getName();
    SORT_TAG = type + FilterSortFragment.class.getName();
    PRICE_TAG = type + FilterPriceFragment.class.getName();
    MORE_TAG = type + FilterMoreFragment.class.getName();
  }

  private void requestSupportBrands() {
    if (!BeeUtils.isNetAvailable()) {
      BeeUtils.showToast(R.string.bee_net_unreachable);
      isRequestBrandData = false;
      return;
    }
    Map<String, Object> params = ParamsUtil.getSupportBrand();
    API.post(params, new HttpCallBack() {
      @Override public void onFinish(String response) {
        handleSupportBrands(response);
      }
    });
  }

  private void handleSupportBrands(String response) {
    if (!TextUtils.isEmpty(response)) {
      List<BrandEntity> brands = GsonParse.parseBrand(response);
      DbUtils.updateBrand(brands);
      SpUtils.setLastCityForBrand(SpUtils.getCurrentCity());
      isRequestBrandData = false;
      removeBrandFragment();
      showBrandFragment();
    } else {
      isRequestBrandData = false;
    }
  }

  private void removeBrandFragment() {
    try {
      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      FilterBrandFragment brandFragment;
      if (getSupportFragmentManager().findFragmentByTag(BRAND_TAG) != null) {
        brandFragment =
            (FilterBrandFragment) getSupportFragmentManager().findFragmentByTag(BRAND_TAG);
        mTransaction.remove(brandFragment);
      }
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    } catch (Exception e) {
      //
    }
  }

  /** 显示品牌选择Fragment */
  public void showBrandFragment() {
    //如果当前正在请求次brandFragment的data，返回
    if (isRequestBrandData) return;
    hideSortFragment();
    hidePriceFragment();
    hideMoreFragment();
    int cityId = SpUtils.getLastCityForBrand();
    if (cityId != SpUtils.getCurrentCity()) {
      isRequestBrandData = true;
      requestSupportBrands();
    } else {
      try {
        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.setCustomAnimations(R.anim.trans_in_down_up, R.anim.trans_out_up_down);
        FilterBrandFragment brandFragment;
        if (getSupportFragmentManager().findFragmentByTag(BRAND_TAG) == null) {
          brandFragment = FilterBrandFragment.newInstance();
          brandFragment.setType(type);
          mTransaction.add(mFrameLayoutId, brandFragment, BRAND_TAG);
        } else {
          brandFragment =
              (FilterBrandFragment) getSupportFragmentManager().findFragmentByTag(BRAND_TAG);
          brandFragment.setType(type);
          mTransaction.show(brandFragment);
        }
        mTransaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
      } catch (Exception e) {
        //
      }
    }
  }

  private void hideBrandFragment() {
    hideCarSeriesFragment();
    if (getSupportFragmentManager().findFragmentByTag(BRAND_TAG) != null) {
      try {
        Fragment brandFragment = getSupportFragmentManager().findFragmentByTag(BRAND_TAG);
        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.setCustomAnimations(R.anim.trans_in_down_up, R.anim.trans_out_up_down);
        mTransaction.hide(brandFragment);
        mTransaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
      } catch (Exception e) {
        //
      }
    }
  }

  private void showCarSeriesFragment(BrandEntity mBrand) {
    try {
      int brand_id = mBrand.getBrand_id();
      String brand_name = mBrand.getBrand_name();
      List<SeriesEntity> seriesData = SeriesDAO.getInstance().findSeriesById(mBrand.getSeries());
      FragmentManager mManager = getSupportFragmentManager();
      FragmentTransaction mTransaction = mManager.beginTransaction();
      FilterCarSeriesFragment showCariesFragment;
      showCariesFragment = FilterCarSeriesFragment.newInstance();
      showCariesFragment.setType(type);
      mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
      if (!showCariesFragment.isAdded()) {
        mTransaction.add(mFrameLayoutId, showCariesFragment, SERIES_TAG);
      }

      mTransaction.show(showCariesFragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
      showCariesFragment.setCarSeries(brand_id, brand_name, seriesData);
    } catch (Exception e) {
      //
    }
  }

  private void hideCarSeriesFragment() {
    if (getSupportFragmentManager().findFragmentByTag(SERIES_TAG) != null) {
      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
      Fragment fragment = getSupportFragmentManager().findFragmentByTag(SERIES_TAG);
      mTransaction.hide(fragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    }
  }

  /** 显示排序的Fragment */
  private void showSortFragment() {
    hideBrandFragment();
    hidePriceFragment();
    hideMoreFragment();
    FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
    mTransaction.setCustomAnimations(R.anim.trans_in_down_up, R.anim.trans_out_up_down);
    FilterSortFragment sortFilterFragment;
    if (getSupportFragmentManager().findFragmentByTag(SORT_TAG) == null) {
      sortFilterFragment = FilterSortFragment.newInstance();
      sortFilterFragment.setType(type);
      mTransaction.add(mFrameLayoutId, sortFilterFragment, SORT_TAG);
    } else {
      sortFilterFragment =
          (FilterSortFragment) getSupportFragmentManager().findFragmentByTag(SORT_TAG);
      sortFilterFragment.setType(type);
      mTransaction.show(sortFilterFragment);
    }
    mTransaction.commitAllowingStateLoss();
    getSupportFragmentManager().executePendingTransactions();
  }

  private void hideSortFragment() {
    if (getSupportFragmentManager().findFragmentByTag(SORT_TAG) != null) {
      Fragment sortFragment = getSupportFragmentManager().findFragmentByTag(SORT_TAG);
      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      mTransaction.setCustomAnimations(R.anim.trans_in_down_up, R.anim.trans_out_up_down);
      mTransaction.hide(sortFragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    }
  }

  /** 显示价格的Fragment */
  private void showPriceFragment() {
    hideSortFragment();
    hideBrandFragment();
    hideMoreFragment();
    FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
    mTransaction.setCustomAnimations(R.anim.trans_in_down_up, R.anim.trans_out_up_down);
    FilterPriceFragment priceFilterFragment;
    if (getSupportFragmentManager().findFragmentByTag(PRICE_TAG) == null) {
      priceFilterFragment = FilterPriceFragment.newInstance();
      priceFilterFragment.setType(type);
      mTransaction.add(mFrameLayoutId, priceFilterFragment, PRICE_TAG);
    } else {
      priceFilterFragment =
          (FilterPriceFragment) getSupportFragmentManager().findFragmentByTag(PRICE_TAG);
      priceFilterFragment.setType(type);
      mTransaction.show(priceFilterFragment);
    }
    mTransaction.commitAllowingStateLoss();
    getSupportFragmentManager().executePendingTransactions();
  }

  private void hidePriceFragment() {
    if (getSupportFragmentManager().findFragmentByTag(PRICE_TAG) != null) {
      Fragment priceFragment = getSupportFragmentManager().findFragmentByTag(PRICE_TAG);
      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      mTransaction.setCustomAnimations(R.anim.trans_in_down_up, R.anim.trans_out_up_down);
      mTransaction.hide(priceFragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    }
  }

  /** 显示更多的Fragment */
  private void showMoreFragment() {
    hideSortFragment();
    hidePriceFragment();
    hideBrandFragment();
    FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
    mTransaction.setCustomAnimations(R.anim.trans_in_down_up, R.anim.trans_out_up_down);
    FilterMoreFragment moreFilterFragment;
    if (getSupportFragmentManager().findFragmentByTag(MORE_TAG) == null) {
      moreFilterFragment = FilterMoreFragment.newInstance();
      moreFilterFragment.setType(type);
      mTransaction.add(mFrameLayoutId, moreFilterFragment, MORE_TAG);
    } else {
      moreFilterFragment =
          (FilterMoreFragment) getSupportFragmentManager().findFragmentByTag(MORE_TAG);
      moreFilterFragment.setType(type);
      mTransaction.show(moreFilterFragment);
    }
    mTransaction.commitAllowingStateLoss();
    getSupportFragmentManager().executePendingTransactions();
    //只要打开该界面，就发送通知到MoreFilterFragment去请求数据
    BeeEvent.postEvent(FilterUtils.ALL + BeeEvent.ACTION_REQUEST_MORE_DATA);
  }

  private void hideMoreFragment() {
    if (getSupportFragmentManager().findFragmentByTag(MORE_TAG) != null) {
      Fragment moreFragment = getSupportFragmentManager().findFragmentByTag(MORE_TAG);
      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      mTransaction.setCustomAnimations(R.anim.trans_in_down_up, R.anim.trans_out_up_down);
      mTransaction.hide(moreFragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    }
  }

  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN)
  public void commonFilterBarEvent(EventBusEntity entity) {
    String action = entity.getAction();
    initTags(entity.getIntValue());
    switch (action) {
      case BeeEvent.ACTION_SHOW_BRAND_FRAGMENT:
        showBrandFragment();
        break;
      case BeeEvent.ACTION_HIDE_BRAND_FRAGMENT:
        hideBrandFragment();
        break;
      case BeeEvent.ACTION_SHOW_SORT_FRAGMENT:
        showSortFragment();
        break;
      case BeeEvent.ACTION_HIDE_SORT_FRAGMENT:
        hideSortFragment();
        break;
      case BeeEvent.ACTION_SHOW_PRICE_FRAGMENT:
        showPriceFragment();
        break;
      case BeeEvent.ACTION_HIDE_PRICE_FRAGMENT:
        hidePriceFragment();
        break;
      case BeeEvent.ACTION_SHOW_MORE_FRAGMENT:
        showMoreFragment();
        break;
      case BeeEvent.ACTION_HIDE_MORE_FRAGMENT:
        hideMoreFragment();
        break;
    }
  }

  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN)
  public void filterChooseEvent(EventBusEntity entity) {
    if (entity != null) {
      String action = entity.getAction();
      String sortAction = type + BeeEvent.ACTION_SORT_CHOOSE;
      String brandAction = type + BeeEvent.ACTION_BRAND_CHOOSE;
      String seriesAction = type + BeeEvent.ACTION_CAR_SERIES_CHOOSE;
      String priceAction = type + BeeEvent.ACTION_PRICE_CHOOSE;
      String moreAction = type + BeeEvent.ACTION_MORE_CHOOSE;

      if (action.equals(sortAction)) {
        hideSortFragment();
      } else if (action.equals(brandAction)) {
        Object mBrand = entity.getObjValue();
        if (mBrand == null) {
          FilterUtils.saveBrandFilterTerm(type, 0, 0);
          hideBrandFragment();
          BeeEvent.postEvent(type + BeeEvent.ACTION_FILTER_CHANGE);
          BeeEvent.postEvent(type + BeeEvent.ACTION_BRAND_CHOOSE_CHANGE);
        } else {
          BrandEntity convertBrand = (BrandEntity) mBrand;
          showCarSeriesFragment(convertBrand);
        }
      } else if (action.equals(seriesAction)) {
        hideCarSeriesFragment();
        Object objValue = entity.getObjValue();
        if (objValue != null) {
          hideBrandFragment();
          SeriesEntity seriesEntity = (SeriesEntity) objValue;
          int brand_id = seriesEntity.getBrand_id();
          int series_id = seriesEntity.getId();
          FilterUtils.saveBrandFilterTerm(type, brand_id, series_id);
          BeeEvent.postEvent(type + BeeEvent.ACTION_FILTER_CHANGE);
          BeeEvent.postEvent(type + BeeEvent.ACTION_BRAND_CHOOSE_CHANGE);
        }
      } else if (action.equals(priceAction)) {
        hidePriceFragment();
        BeeEvent.postEvent(type + BeeEvent.ACTION_FILTER_CHANGE);
      } else if (action.equals(moreAction)) {
        hideMoreFragment();
        BeeEvent.postEvent(type + BeeEvent.ACTION_FILTER_CHANGE);
      }
    }
  }

  /** ======================== 以下是城市关的东西 ============================== */
  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN) public void cityChooseEvent(
      EventBusEntity entity) {
    String action = entity.getAction();
    switch (action) {
      case BeeEvent.ACTION_SHOW_CITY_FRAGMENT:
        showCityFragment();
        break;
      case BeeEvent.ACTION_HIDE_CITY_FRAGMENT:
        hideCityFragment();
        break;
    }
  }

  private final String CITY_TAG = CityFragment.class.getSimpleName();

  /** 显示城市的Fragment */
  private void showCityFragment() {
    FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
    mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
    CityFragment cityFragment;
    if (getSupportFragmentManager().findFragmentByTag(CITY_TAG) == null) {
      cityFragment = new CityFragment();
      mTransaction.add(mFrameLayoutId, cityFragment, CITY_TAG);
    } else {
      cityFragment = (CityFragment) getSupportFragmentManager().findFragmentByTag(CITY_TAG);
      mTransaction.show(cityFragment);
    }
    mTransaction.commitAllowingStateLoss();
    getSupportFragmentManager().executePendingTransactions();
  }

  private void hideCityFragment() {
    if (getSupportFragmentManager().findFragmentByTag(CITY_TAG) != null) {
      Fragment cityFragment = getSupportFragmentManager().findFragmentByTag(CITY_TAG);
      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      mTransaction.setCustomAnimations(R.anim.trans_in_right_left, R.anim.trans_out_left_right);
      mTransaction.hide(cityFragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    }
  }

  /** ======================== 以下是平台关的东西 ============================== */
  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN)
  public void platformChooseEvent(EventBusEntity entity) {
    String action = entity.getAction();
    switch (action) {
      case BeeEvent.ACTION_SHOW_PLATFORM_FRAGMENT:
        showPlatformFragment(entity.getIntValue());
        break;
      case BeeEvent.ACTION_HIDE_PLATFORM_FRAGMENT:
        hidePlatformFragment();
        break;
    }
  }

  private final String PLATFORM_TAG = PlatformFragment.class.getSimpleName();

  /** 显示城市的Fragment */
  private void showPlatformFragment(int position) {
    FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
    mTransaction.setCustomAnimations(R.anim.trans_in_down_up, R.anim.trans_out_up_down);
    PlatformFragment platformFragment;
    if (getSupportFragmentManager().findFragmentByTag(PLATFORM_TAG) == null) {
      platformFragment = new PlatformFragment();
      mTransaction.add(mFrameLayoutId, platformFragment, PLATFORM_TAG);
    } else {
      platformFragment =
          (PlatformFragment) getSupportFragmentManager().findFragmentByTag(PLATFORM_TAG);
      mTransaction.show(platformFragment);
    }
    mTransaction.commitAllowingStateLoss();
    getSupportFragmentManager().executePendingTransactions();
    BeeEvent.postEvent(BeeEvent.ACTION_PLATFORM_CHOOSE_CHANGE, position);
  }

  private void hidePlatformFragment() {
    if (getSupportFragmentManager().findFragmentByTag(PLATFORM_TAG) != null) {
      Fragment platformFragment = getSupportFragmentManager().findFragmentByTag(PLATFORM_TAG);
      FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
      mTransaction.setCustomAnimations(R.anim.trans_in_down_up, R.anim.trans_out_up_down);
      mTransaction.hide(platformFragment);
      mTransaction.commitAllowingStateLoss();
      getSupportFragmentManager().executePendingTransactions();
    }
  }
}
