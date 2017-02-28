package com.haoche51.bee.adapter;

import android.content.Context;
import android.view.View;
import com.haoche51.bee.R;
import java.util.List;

public class PlatformAdapter extends BeeAdapter<String> {

  public PlatformAdapter(Context context, List<String> data, int layoutId) {
    super(context, data, layoutId);
  }

  @Override public void fillViewData(BeeViewHolder holder, final int position) {
    holder.setTextViewText(R.id.tv_platform_item, getItem(position));
    View topLine = holder.findTheView(R.id.v_platform_top_item);
    View bottomLine = holder.findTheView(R.id.v_platform_bottom_item);
    if (position == 0) {
      topLine.setVisibility(View.INVISIBLE);
    } else {
      topLine.setVisibility(View.VISIBLE);
    }
    if (position == getCount() - 1) {
      bottomLine.setVisibility(View.GONE);
    } else {
      bottomLine.setVisibility(View.VISIBLE);
    }
  }
}
