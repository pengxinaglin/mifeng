package com.haoche51.bee.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.haoche51.bee.R;
import com.haoche51.bee.adapter.FilterPriceAdapter;
import com.haoche51.bee.custom.BeeGridView;
import com.haoche51.bee.custom.SeekBarPressure;
import com.haoche51.bee.custom.ViewClickListener;
import com.haoche51.bee.dao.FilterTerm;
import com.haoche51.bee.entity.EventBusEntity;
import com.haoche51.bee.entity.KeyValueEntity;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeConstants;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.ViewUtils;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FilterPriceFragment extends BeeFragment {

  @BindView(R.id.iv_filter_price_close) View mCloseIv;
  @BindView(R.id.tv_all_price) TextView unlimitedTv;
  @BindView(R.id.iv_all_price_choose) ImageView unlimitedIv;
  @BindView(R.id.gv_price_main) BeeGridView mPriceGv;
  @BindView(R.id.tv_custom_price_hint) TextView mCustomHint;
  @BindView(R.id.sb_filter_price) SeekBarPressure mSeekBar;
  @BindView(R.id.tv_sb_price_hint) TextView mSbHintTv;
  @BindView(R.id.tv_seek_bar_ensure) TextView mEnsureTv;

  private FilterPriceAdapter mAdapter;
  private List<KeyValueEntity> mData = new ArrayList<>();
  private int priceLow;
  private int priceHigh;
  private double dProgressLow;
  private double dProgressHigh;

  //自定义seekBar的默认数值，以及第二个滑块默认位置
  private static final int INIT_VALUE = 51;
  private int type;

  public void setType(int type) {
    this.type = type;
  }

  public static FilterPriceFragment newInstance() {
    return new FilterPriceFragment();
  }

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_filter_price;
  }

  @Override void doInitViewOrData() {
    List<KeyValueEntity> data = FilterUtils.getDefaultSortData(BeeConstants.FILTER_PRICE);
    mData.clear();
    mData.addAll(data);
    int layoutId = R.layout.gvitem_filter_price;
    mAdapter = new FilterPriceAdapter(getActivity(), mData, layoutId, type, mClickListener);
    mPriceGv.setAdapter(mAdapter);

    unlimitedTv.setOnClickListener(mClickListener);
    mCloseIv.setOnClickListener(mClickListener);
    mEnsureTv.setOnClickListener(mClickListener);
    int color = BeeUtils.getResColor(R.color.common_text_gray);
    ViewUtils.changeTextViewColor(mCustomHint, color, 3, 6);
    initSeekBar();
    setUnLimitedStatus();
  }

  private ViewClickListener mClickListener = new ViewClickListener() {
    @Override public void performViewClick(View v) {
      mEnsureTv.setVisibility(View.GONE);
      int vid = v.getId();
      switch (vid) {
        case R.id.tv_all_price:
          FilterUtils.saveTermPrice(type, 0, 0);
          BeeEvent.postEvent(type + BeeEvent.ACTION_PRICE_CHOOSE);
          setUnLimitedStatus();
          resetSeekBar();
          mAdapter.notifyDataSetChanged();
          BeeStatistics.priceClick("价格不限");
          break;
        case R.id.linear_filter_price_item:
          KeyValueEntity entity = (KeyValueEntity) v.getTag();
          String key = entity.getKey();
          FilterUtils.priceKey2FilterTerm(type, key);
          BeeEvent.postEvent(type + BeeEvent.ACTION_PRICE_CHOOSE);
          resetSeekBar();
          mAdapter.notifyDataSetChanged();
          setUnLimitedStatus();
          BeeStatistics.priceClick("快速选择" + key);
          break;
        case R.id.iv_filter_price_close:
          BeeEvent.postEvent(BeeEvent.ACTION_HIDE_PRICE_FRAGMENT);
          BeeStatistics.closeClick("价格");
          break;
        case R.id.tv_seek_bar_ensure:
          if (priceHigh == INIT_VALUE) {
            priceHigh = 0;
          }
          FilterUtils.saveTermPrice(type, priceLow, priceHigh);
          mAdapter.notifyDataSetChanged();
          setUnLimitedStatus();
          BeeEvent.postEvent(type + BeeEvent.ACTION_PRICE_CHOOSE);
          BeeStatistics.priceClick("滑动块" + priceLow + "~" + priceHigh + "万");
          break;
      }
    }
  };

  private void setUnLimitedStatus() {
    FilterTerm term = FilterUtils.getFilterTerm(type);
    String priceKey = FilterUtils.getFilterTermString(term, BeeConstants.FILTER_PRICE);
    if (BeeConstants.UNLIMITED.equals(priceKey)) {
      unlimitedIv.setVisibility(View.VISIBLE);
      unlimitedTv.setTextColor(BeeUtils.getResColor(R.color.common_yellow));
    } else {
      unlimitedIv.setVisibility(View.GONE);
      unlimitedTv.setTextColor(BeeUtils.getResColor(R.color.common_text_black));
    }
  }

  private void resetSeekBar() {
    mSeekBar.setProgressHigh(INIT_VALUE);
    mSeekBar.setProgressLow(0);
    mSbHintTv.setTextColor(BeeUtils.getResColor(R.color.common_text_black));
    mSbHintTv.setText(BeeConstants.UNLIMITED);
  }

  private void initSeekBar() {
    mSeekBar.setDefaultScale(INIT_VALUE);
    mSeekBar.setProgressHigh(INIT_VALUE);
    mSeekBar.setType(SeekBarPressure.PRICE);
    mSeekBar.setOnSeekBarChangeListener(new SeekBarPressure.OnSeekBarChangeListener() {
      @Override public void onProgressBefore() {

      }

      @Override public void onProgressChanged(SeekBarPressure seekBar, double progressLow,
          double progressHigh) {
        priceLow = (int) progressLow;
        priceHigh = (int) progressHigh;
        dProgressLow = progressLow;
        dProgressHigh = progressHigh;
        String price = FilterUtils.getPrice(priceLow, priceHigh);
        if (priceHigh == INIT_VALUE) {
          price = FilterUtils.getPrice(priceLow, 0);
        }
        if (BeeConstants.UNLIMITED.equals(price)) {
          mSbHintTv.setTextColor(BeeUtils.getResColor(R.color.common_text_black));
          mSbHintTv.setText(price);
        } else {
          mSbHintTv.setTextColor(BeeUtils.getResColor(R.color.common_yellow));
          mSbHintTv.setText(price);
        }
      }

      @Override public void onProgressAfter() {
        mSeekBar.setProgressHigh(dProgressHigh);
        mSeekBar.setProgressLow(dProgressLow);
        mEnsureTv.setVisibility(View.VISIBLE);
      }
    });
  }

  @OnClick(R.id.linear_price_parent) public void setNull() {
  }

  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN)
  public void priceChooseChangeEvent(EventBusEntity entity) {
    String action = entity.getAction();
    if (action.equals(type + BeeEvent.ACTION_PRICE_CHOOSE_CHANGE)) {
      setUnLimitedStatus();
      resetSeekBar();
      mAdapter.notifyDataSetChanged();
    }
  }
}
