package com.haoche51.bee.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.haoche51.bee.entity.EventBusEntity;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.BeeLog;
import com.haoche51.bee.util.BeeStatistics;
import java.lang.reflect.Field;

public abstract class BeeFragment extends Fragment {

  protected Activity mCurrentAct;
  protected final String TAG = this.getClass().getSimpleName();
  protected View mFragmentContentView;
  protected LayoutInflater mLayoutInflater;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (isNeedBindEventBus()) {
      BeeEvent.register(this);
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mLayoutInflater = inflater;
    if (getFragmentContentViewResourceId() > 0) {
      mFragmentContentView = inflater.inflate(getFragmentContentViewResourceId(), container, false);
    } else {
      mFragmentContentView = super.onCreateView(inflater, container, savedInstanceState);
    }
    return mFragmentContentView;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    mCurrentAct = activity;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);

    long costTime = System.currentTimeMillis();
    doInitViewOrData();
    costTime = System.currentTimeMillis() - costTime;

    BeeLog.d(TAG, TAG + " doInitViewOrData cost " + costTime + " \n");
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (isNeedBindEventBus()) {
      BeeEvent.unRegister(this);
    }
  }

  @Override public void onDetach() {
    super.onDetach();
    try {
      Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
      childFragmentManager.setAccessible(true);
      childFragmentManager.set(this, null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected void cancelEvent(EventBusEntity entity) {
    BeeEvent.cancelDelivery(entity);
  }

  @SuppressWarnings("unused") public View getFragmentContentView() {
    return mFragmentContentView;
  }

  abstract boolean isNeedBindEventBus();

  abstract int getFragmentContentViewResourceId();

  abstract void doInitViewOrData();

  protected boolean isCurrentActivityValid() {
    return getActivity() != null && !getActivity().isFinishing();
  }

  @Override public void onResume() {
    super.onResume();
    BeeStatistics.onPageStart(getClass().getSimpleName());
  }

  @Override public void onPause() {
    super.onPause();
    BeeStatistics.onPageEnd(getClass().getSimpleName());
  }
}
