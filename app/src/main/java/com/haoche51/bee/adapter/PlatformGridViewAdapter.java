package com.haoche51.bee.adapter;

import android.content.Context;
import android.view.View;
import com.haoche51.bee.R;
import java.util.List;

public class PlatformGridViewAdapter extends BeeAdapter<String> {

  public PlatformGridViewAdapter(Context context, List<String> data, int layoutId) {
    super(context, data, layoutId);
  }

  @Override public void fillViewData(BeeViewHolder holder, final int position) {
    holder.setTextViewText(R.id.platform_advantage_item, getItem(position));
  }
}
