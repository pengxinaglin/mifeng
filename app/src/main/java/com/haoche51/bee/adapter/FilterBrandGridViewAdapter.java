package com.haoche51.bee.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.haoche51.bee.R;
import com.haoche51.bee.entity.BrandEntity;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeConstants;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.ViewUtils;
import java.util.List;

public class FilterBrandGridViewAdapter extends BeeAdapter<BrandEntity> {

  public FilterBrandGridViewAdapter(Context context, List<BrandEntity> data, int layoutId,
      int type) {
    super(context, data, layoutId);
    this.type = type;
  }

  private View.OnClickListener mListener;
  private int type;

  public void setListener(View.OnClickListener mListener) {
    this.mListener = mListener;
  }

  @Override public void fillViewData(BeeViewHolder holder, int position) {
    BrandEntity entity = getItem(position);
    setBrand(entity, holder);
  }

  private void setBrand(final BrandEntity entity, BeeViewHolder holder) {
    String brandName = entity.getBrand_name();
    int brandId = entity.getBrand_id();
    int resId = ViewUtils.getBrandResID(brandId);
    TextView mBrandTv = holder.findTheView(R.id.tv_filter_brand_item);
    LinearLayout layout = holder.findTheView(R.id.linear_filter_brand_item);
    boolean choose = FilterUtils.getFilterTerm(type).getBrand_id() == brandId;
    int mTextColor;
    if (choose) {
      mTextColor = R.color.common_yellow;
      layout.setBackgroundResource(R.drawable.common_bg_item_choose);
    } else {
      mTextColor = R.color.common_text_black;
      layout.setBackgroundResource(R.drawable.common_bg_gray);
    }
    int color = BeeUtils.getResColor(mTextColor);
    mBrandTv.setTextColor(color);
    if (TextUtils.isEmpty(brandName)) {
      brandName = "未知";
    }
    mBrandTv.setText(brandName);
    ViewUtils.setTextViewDrawable(mBrandTv, resId, BeeConstants.DRAWABLE_TOP, 7 / 10F);

    layout.setTag(entity);
    layout.setOnClickListener(mListener);
  }
}
