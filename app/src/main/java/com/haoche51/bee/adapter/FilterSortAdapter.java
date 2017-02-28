package com.haoche51.bee.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.haoche51.bee.R;
import com.haoche51.bee.entity.KeyValueEntity;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.FilterUtils;
import java.util.List;

public class FilterSortAdapter extends BeeAdapter<KeyValueEntity> {

  private int type;

  public FilterSortAdapter(Context context, List<KeyValueEntity> data, int layoutId, int type) {
    super(context, data, layoutId);
    this.type = type;
  }

  @Override public void fillViewData(BeeViewHolder holder, final int position) {
    KeyValueEntity entity = getItem(position);
    final String value = entity.getKey();
    boolean needRedColor = value.equals(FilterUtils.getFilterTerm(type).getDescriptionSort());
    int color = needRedColor ? R.color.common_yellow : R.color.common_text_black;
    int visible = needRedColor ? View.VISIBLE : View.GONE;
    holder.findTheView(R.id.iv_sort_item).setVisibility(visible);
    TextView mTextView = holder.findTheView(R.id.tv_sort_item);
    mTextView.setTextColor(BeeUtils.getResColor(color));
    mTextView.setText(entity.getKey());
    mTextView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        FilterUtils.saveSort(type, value);
        notifyDataSetChanged();
        BeeEvent.postEvent(type + BeeEvent.ACTION_SORT_CHOOSE);
        BeeStatistics.sortClick(value);
      }
    });
  }
}
