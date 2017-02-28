package com.haoche51.bee.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.haoche51.bee.R;
import com.haoche51.bee.entity.CityEntity;
import java.util.List;

public class CityGridViewAdapter extends BeeAdapter<CityEntity> {

  public CityGridViewAdapter(Context context, List<CityEntity> data, int layoutId) {
    super(context, data, layoutId);
  }

  private View.OnClickListener mListener;

  public void setListener(View.OnClickListener mListener) {
    this.mListener = mListener;
  }

  @Override public void fillViewData(BeeViewHolder holder, int position) {
    CityEntity entity = getItem(position);
    setCity(entity, holder);
  }

  private void setCity(final CityEntity entity, BeeViewHolder holder) {
    String brandName = entity.getCity_name();
    TextView mBrandTv = holder.findTheView(R.id.tv_city_item);
    RelativeLayout layout = holder.findTheView(R.id.rel_city_item);
    mBrandTv.setText(brandName);

    layout.setTag(entity);
    layout.setOnClickListener(mListener);
  }
}
