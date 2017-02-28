package com.haoche51.bee.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.haoche51.bee.R;
import com.haoche51.bee.dao.FilterTerm;
import com.haoche51.bee.entity.KeyValueEntity;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeConstants;
import com.haoche51.bee.util.FilterUtils;
import java.util.List;

public class FilterPriceAdapter extends BeeAdapter<KeyValueEntity> {

  private int type;

  private View.OnClickListener mClickListener;

  public FilterPriceAdapter(Context context, List<KeyValueEntity> data, int layoutId, int type,
      View.OnClickListener listener) {
    super(context, data, layoutId);
    this.type = type;
    this.mClickListener = listener;
  }

  @Override public void fillViewData(BeeViewHolder holder, int position) {
    FilterTerm term = FilterUtils.getFilterTerm(type);
    KeyValueEntity entity = getItem(position);
    TextView nameTv = holder.findTheView(R.id.tv_price_item);
    LinearLayout layout = holder.findTheView(R.id.linear_filter_price_item);
    nameTv.setText(entity.getKey());
    layout.setTag(entity);
    layout.setOnClickListener(mClickListener);
    String priceKey = FilterUtils.getFilterTermString(term, BeeConstants.FILTER_PRICE);
    int textColor;
    if (entity.getKey().equals(priceKey)) {
      textColor = R.color.common_yellow;
      layout.setBackgroundResource(R.drawable.common_bg_item_choose);
    } else {
      textColor = R.color.common_text_black;
      layout.setBackgroundResource(R.drawable.common_bg_gray);
    }
    nameTv.setTextColor(BeeUtils.getResColor(textColor));
  }
}
