package com.haoche51.bee.adapter;

import android.content.Context;
import android.view.View;
import com.haoche51.bee.R;
import com.haoche51.bee.entity.VehicleItemEntity;
import com.haoche51.bee.util.BeeUtils;
import java.util.List;

public class AskVehicleAdapter extends SinglePicVehicleAdapter {

  public AskVehicleAdapter(Context context, List<VehicleItemEntity> data, int layoutId) {
    super(context, data, layoutId);
  }

  @Override public void fillOtherData(BeeViewHolder holder, int position) {
    //设置是否已经售出标签
    VehicleItemEntity entity = getItem(position);
    View soldIv = holder.findTheView(R.id.iv_single_pic_vehicle_sold);
    int status = BeeUtils.str2Int(entity.getStatus());
    boolean isSold = status != 1;
    int visibleStatus = isSold ? View.VISIBLE : View.GONE;
    soldIv.setVisibility(visibleStatus);
  }
}
