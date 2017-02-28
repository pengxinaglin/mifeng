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
import com.haoche51.bee.util.FilterUtils;
import java.util.ArrayList;
import java.util.List;

public class FilterPlatFormAdapter extends BeeAdapter<KeyValueEntity> {

  private int type;
  private View.OnClickListener mListener;

  public void setListener(View.OnClickListener mListener) {
    this.mListener = mListener;
  }

  public FilterPlatFormAdapter(Context context, List<KeyValueEntity> data, int layoutId, int type) {
    super(context, data, layoutId);
    this.type = type;
  }

  @Override public void fillViewData(BeeViewHolder holder, final int position) {
    FilterTerm term = FilterUtils.getFilterTerm(type);
    KeyValueEntity entity = getItem(position);
    RelativeLayout layout = holder.findTheView(R.id.rel_filter_platform_item);
    TextView name = holder.findTheView(R.id.tv_filter_platform_item);
    ImageView cancel = holder.findTheView(R.id.iv_filter_platform_item);
    List<String> key = term.getPlatform();
    if (key == null) key = new ArrayList<>();
    int textColor;
    if (key.contains(entity.getKey())) {
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
    layout.setTag(R.id.rel_filter_platform_item, entity.getKey());
    layout.setOnClickListener(mListener);
  }
}
