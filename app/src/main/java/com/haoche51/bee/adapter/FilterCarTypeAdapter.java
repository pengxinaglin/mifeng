package com.haoche51.bee.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.haoche51.bee.R;
import com.haoche51.bee.dao.FilterTerm;
import com.haoche51.bee.entity.KeyValueEntity;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeConstants;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.ViewUtils;
import java.util.List;

public class FilterCarTypeAdapter extends BeeAdapter<KeyValueEntity> {

  private int type;
  private View.OnClickListener mListener;

  public void setListener(View.OnClickListener mListener) {
    this.mListener = mListener;
  }

  public FilterCarTypeAdapter(Context context, List<KeyValueEntity> data, int layoutId, int type) {
    super(context, data, layoutId);
    this.type = type;
  }

  @Override public void fillViewData(BeeViewHolder holder, final int position) {
    FilterTerm term = FilterUtils.getFilterTerm(type);
    KeyValueEntity entity = getItem(position);
    RelativeLayout layout = holder.findTheView(R.id.rel_filter_car_type_item);
    TextView name = holder.findTheView(R.id.tv_filter_car_type_item);
    ImageView cancel = holder.findTheView(R.id.iv_filter_car_type_item);
    int carTypeId = ViewUtils.getCarTypeResID(position + 1);
    if (carTypeId != 0) {
      ViewUtils.setTextViewDrawable(name, carTypeId, BeeConstants.DRAWABLE_TOP, 2 / 3F);
    }
    String key = FilterUtils.getFilterTermString(term, BeeConstants.FILTER_CAR_TYPE);
    int textColor;
    if (entity.getKey().equals(key)) {
      textColor = R.color.common_yellow;
      cancel.setVisibility(View.VISIBLE);
      layout.setBackgroundResource(R.drawable.common_bg_item_choose);
    } else {
      textColor = R.color.common_text_black;
      cancel.setVisibility(View.GONE);
      layout.setBackgroundResource(R.drawable.common_bg_gray);
    }
    name.setTextColor(BeeUtils.getResColor(textColor));
    name.setText(entity.getKey());
    layout.setTag(R.id.rel_filter_car_type_item, position + 1);
    layout.setOnClickListener(mListener);
  }
}
