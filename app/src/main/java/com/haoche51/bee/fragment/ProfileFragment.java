package com.haoche51.bee.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import butterknife.BindView;
import com.haoche51.bee.R;
import com.haoche51.bee.adapter.AskVehicleAdapter;
import com.haoche51.bee.entity.AskEntity;
import com.haoche51.bee.entity.VehicleItemEntity;
import com.haoche51.bee.net.API;
import com.haoche51.bee.net.GsonParse;
import com.haoche51.bee.net.HttpCallBack;
import com.haoche51.bee.net.ParamsUtil;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.SpUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 我的咨询的Fragment */
public class ProfileFragment extends BeeFragment {

  @BindView(R.id.lv_my_ask_main) ListView mMainLv;
  @BindView(R.id.linear_empty_view_profile) View emptyView;

  private List<VehicleItemEntity> mVehicleData = new ArrayList<>();
  private AskVehicleAdapter mAdapter;

  private String mLastAsk = "";

  @Override boolean isNeedBindEventBus() {
    return false;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_profile;
  }

  @Override void doInitViewOrData() {
    initListView();
    requestData();
  }

  private void initListView() {
    int itemRes = R.layout.lvitem_vehicle_list;
    mAdapter = new AskVehicleAdapter(getActivity(), mVehicleData, itemRes);
    mMainLv.setEmptyView(emptyView);
    mMainLv.setAdapter(mAdapter);
  }

  /** 如果存储为空,则不请求数据 */
  private void requestData() {
    if (BeeUtils.isListEmpty(SpUtils.getAskVehicleIds())) return;
    mLastAsk = SpUtils.getAskString();
    Map<String, Object> cityParams = ParamsUtil.getMyAsk();
    API.post(cityParams, new HttpCallBack() {
      @Override public void onFinish(String responseJsonString) {
        if (!TextUtils.isEmpty(responseJsonString)) {
          handleMyAskData(responseJsonString);
        }
      }
    });
  }

  private void handleMyAskData(String responseJsonString) {
    AskEntity entity = GsonParse.parseAskData(responseJsonString);
    List<VehicleItemEntity> lists = entity.getData();
    if (!BeeUtils.isListEmpty(lists)) {
      mVehicleData.clear();
      mVehicleData.addAll(lists);
      mAdapter.notifyDataSetChanged();
    }
  }

  @Override public void onResume() {
    super.onResume();
    if (!mLastAsk.equals(SpUtils.getAskString())) {
      requestData();
    }
  }
}