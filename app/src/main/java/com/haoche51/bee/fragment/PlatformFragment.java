package com.haoche51.bee.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.haoche51.bee.R;
import com.haoche51.bee.adapter.PlatformAdapter;
import com.haoche51.bee.adapter.PlatformGridViewAdapter;
import com.haoche51.bee.custom.BeeGridView;
import com.haoche51.bee.entity.EventBusEntity;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.ViewUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PlatformFragment extends BeeFragment {

  @BindView(R.id.lv_platform) ListView mPlatformLv;

  private ImageView mIconIv;
  private TextView mNameTv;
  private TextView mDetectionTv;
  private BeeGridView mAdvantageGv;

  private String[] nameList = BeeUtils.getResArray(R.array.platform_name_list);
  private String[] detectionList = BeeUtils.getResArray(R.array.platform_detection_list);
  private int[] advantageId = {
      R.array.platform_advantage_haoche, R.array.platform_advantage_guazi,
      R.array.platform_advantage_youxin, R.array.platform_advantage_youche,
      R.array.platform_advantage_chewang, R.array.platform_advantage_renren,
      R.array.platform_advantage_58
  };
  private int[] introId = {
      R.array.platform_intro_haoche, R.array.platform_intro_guazi, R.array.platform_intro_youxin,
      R.array.platform_intro_youche, R.array.platform_intro_chewang, R.array.platform_intro_renren,
      R.array.platform_intro_58
  };

  private PlatformAdapter adapter;
  private List<String> mData = new ArrayList<>();

  private PlatformGridViewAdapter gvAdapter;
  private List<String> mGvData = new ArrayList<>();

  @OnClick({ R.id.tv_platform_close, R.id.iv_platform_close }) public void hidePlatform() {
    BeeEvent.postEvent(BeeEvent.ACTION_HIDE_PLATFORM_FRAGMENT);
    BeeStatistics.closeClick("平台简介");
  }

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_platform;
  }

  @Override void doInitViewOrData() {

    initHeaderView();

    initListView();
  }

  private void initHeaderView() {
    int resLayout = R.layout.fragment_platform_header;
    View mHeaderView = LayoutInflater.from(getActivity()).inflate(resLayout, null);
    mPlatformLv.addHeaderView(mHeaderView);

    mIconIv = (ImageView) mHeaderView.findViewById(R.id.iv_platform_icon);
    mNameTv = (TextView) mHeaderView.findViewById(R.id.tv_platform_name);
    mDetectionTv = (TextView) mHeaderView.findViewById(R.id.tv_platform_detection);
    mAdvantageGv = (BeeGridView) mHeaderView.findViewById(R.id.gv_platform_advantage);
  }

  private void initListView() {
    mData.add(BeeUtils.getResString(R.string.platform_intro));
    adapter = new PlatformAdapter(getActivity(), mData, R.layout.lvitem_platform);
    mPlatformLv.setAdapter(adapter);

    int gvLayoutId = R.layout.gvitem_platform_advantage;
    gvAdapter = new PlatformGridViewAdapter(getActivity(), mGvData, gvLayoutId);
    mAdvantageGv.setAdapter(gvAdapter);
  }

  @SuppressWarnings("unused") @Subscribe(threadMode = ThreadMode.MAIN)
  public void platformChangeEvent(EventBusEntity entity) {
    if (entity.getAction().equals(BeeEvent.ACTION_PLATFORM_CHOOSE_CHANGE)) {
      changePlatform(entity.getIntValue());
    }
  }

  private void changePlatform(int position) {
    // TODO: 17/1/6 自动滑动到顶部
    if (position >= 0 && position < 7) {
      int iconId = ViewUtils.getPlatformResID(position);
      mIconIv.setImageResource(iconId);
      mNameTv.setText(nameList[position]);
      mDetectionTv.setText(detectionList[position]);

      String[] advantageStr = BeeUtils.getResArray(advantageId[position]);
      String[] introStr = BeeUtils.getResArray(introId[position]);

      if (advantageStr.length > 0) {
        mGvData.clear();
        mGvData.addAll(Arrays.asList(advantageStr));
        gvAdapter.notifyDataSetChanged();
      }

      if (introStr.length > 0) {
        mData.clear();
        mData.addAll(Arrays.asList(introStr));
        adapter.notifyDataSetChanged();
      }
    }
  }
}
