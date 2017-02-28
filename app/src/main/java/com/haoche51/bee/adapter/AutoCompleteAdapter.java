package com.haoche51.bee.adapter;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;
import com.haoche51.bee.R;
import java.util.List;

public class AutoCompleteAdapter extends BeeAdapter<String> implements Filterable {

  public AutoCompleteAdapter(Context context, List data, int layoutId) {
    super(context, data, layoutId);
  }

  @Override public void fillViewData(BeeViewHolder holder, int position) {
    holder.setTextViewText(R.id.tv_search_item, getItem(position));
  }

  @Override public Filter getFilter() {
    return new AutoFilter(this);
  }
}
