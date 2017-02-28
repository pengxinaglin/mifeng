package com.haoche51.bee.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.bee.R;
import com.haoche51.bee.dao.BrandDAO;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeEvent;
import com.haoche51.bee.util.DbUtils;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.ViewUtils;
import java.util.List;

public class HomeBrandGridViewAdapter extends BeeAdapter<Integer> {

  public HomeBrandGridViewAdapter(Context context, List<Integer> data, int layoutId) {
    super(context, data, layoutId);
  }

  @Override public void fillViewData(BeeViewHolder holder, final int position) {
    final int brandId = getItem(position);
    int resId = ViewUtils.getBrandResID(brandId);
    TextView mBrandTv = holder.findTheView(R.id.tv_home_brand_item);
    ImageView mBrandIv = holder.findTheView(R.id.iv_home_brand_item);
    if (position < 3) {
      mBrandTv.setVisibility(View.GONE);
      mBrandIv.setVisibility(View.VISIBLE);
      mBrandIv.setImageResource(resId);
    } else {
      mBrandTv.setVisibility(View.VISIBLE);
      mBrandIv.setVisibility(View.GONE);
    }

    View convertView = holder.getConvertView();
    convertView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        FilterUtils.resetFilterTerm(FilterUtils.ALL);
        if (position < 3) {
          FilterUtils.saveBrandFilterTerm(FilterUtils.ALL, brandId, -1);
          BeeStatistics.homePageClick(DbUtils.getBrandNameById(brandId));
        } else {
          BeeStatistics.homePageClick("全部车源");
        }
        BeeEvent.postEvent(BeeEvent.ACTION_CHANGE_MAIN_TAB);
        BeeEvent.postEvent(FilterUtils.ALL + BeeEvent.ACTION_BRAND_CHOOSE_CHANGE);
      }
    });
  }
}
