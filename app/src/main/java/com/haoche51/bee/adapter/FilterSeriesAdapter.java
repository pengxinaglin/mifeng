package com.haoche51.bee.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.bee.R;
import com.haoche51.bee.entity.SeriesEntity;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.FilterUtils;
import java.util.List;

/**
 * 车系选择
 */
public class FilterSeriesAdapter extends BeeAdapter<SeriesEntity> {

  private View.OnClickListener mClickListener;

  private int type;

  public FilterSeriesAdapter(Context context, List<SeriesEntity> data, int layoutId, int type,
      View.OnClickListener listener) {
    super(context, data, layoutId);
    this.type = type;
    this.mClickListener = listener;
  }

  @Override public void fillViewData(BeeViewHolder holder, int position) {
    SeriesEntity entity = getItem(position);
    TextView nameTv = holder.findTheView(R.id.tv_car_series_item);
    ImageView chooseIv = holder.findTheView(R.id.iv_car_series_choose);
    nameTv.setText(entity.getName());
    nameTv.setTag(entity);
    nameTv.setOnClickListener(mClickListener);
    boolean choose = FilterUtils.getFilterTerm(type).getClass_id() == entity.getId();
    int mTextColor;
    if (choose) {
      mTextColor = R.color.common_yellow;
      chooseIv.setVisibility(View.VISIBLE);
    } else {
      mTextColor = R.color.common_text_black;
      chooseIv.setVisibility(View.GONE);
    }
    int color = BeeUtils.getResColor(mTextColor);
    nameTv.setTextColor(color);
  }
}
