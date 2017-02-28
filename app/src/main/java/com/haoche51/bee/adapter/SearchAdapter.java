package com.haoche51.bee.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.haoche51.bee.R;
import java.util.List;

public class SearchAdapter extends BeeAdapter<String> {

  private View.OnClickListener mClickListener;

  public SearchAdapter(Context context, List<String> data, int layoutId,
      View.OnClickListener listener) {
    super(context, data, layoutId);
    this.mClickListener = listener;
  }

  @Override public void fillViewData(BeeViewHolder holder, int position) {
    String key = getItem(position);
    TextView nameTv = holder.findTheView(R.id.tv_search_common_item);
    RelativeLayout layout = holder.findTheView(R.id.rel_search_common_item);
    nameTv.setText(key);
    layout.setTag(key);
    layout.setOnClickListener(mClickListener);
  }
}
