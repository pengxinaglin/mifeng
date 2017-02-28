package com.haoche51.bee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.haoche51.bee.GlobalData;
import com.haoche51.bee.R;
import com.haoche51.bee.dao.Brand;
import com.haoche51.bee.entity.BrandEntity;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeConstants;
import com.haoche51.bee.util.FilterUtils;
import com.haoche51.bee.util.ViewUtils;
import java.util.List;

public class FilterBrandAdapter extends BaseAdapter {
  private List<Brand> brands;
  private int type;

  public FilterBrandAdapter(List<Brand> brands, View.OnClickListener listener, int type) {
    this.brands = brands;
    this.mClickListener = listener;
    this.type = type;
  }

  private View.OnClickListener mClickListener;

  @Override public int getCount() {
    return brands.size();
  }

  @Override public Object getItem(int position) {
    return brands.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder mHolder;
    if (convertView == null) {
      mHolder = new ViewHolder();
      int resLayout = R.layout.lvitem_filter_brand;
      convertView = LayoutInflater.from(GlobalData.mContext).inflate(resLayout, parent, false);
      mHolder.topLinear = (LinearLayout) convertView.findViewById(R.id.linear_brand_item);
      mHolder.brandName = (TextView) convertView.findViewById(R.id.brand_name);
      mHolder.brandChoose = (ImageView) convertView.findViewById(R.id.iv_brand_choose);
      mHolder.IndexLetter = (TextView) convertView.findViewById(R.id.brand_letter);
      mHolder.line = convertView.findViewById(R.id.brand_line);
      RelativeLayout.LayoutParams params =
          (RelativeLayout.LayoutParams) mHolder.line.getLayoutParams();
      //品牌名字下面那条线距离左边的距离为图片的距离+两边的空白
      params.leftMargin =
          70 * BeeUtils.getScreenWidthInPixels() / 750 + BeeUtils.getDimenPixels(R.dimen.px_30dp);
      convertView.setTag(mHolder);
      mHolder.IndexLetter.getPaint().setFakeBoldText(true);
    } else {
      mHolder = (ViewHolder) convertView.getTag();
    }

    Brand mBrand = brands.get(position);
    boolean isChoose = FilterUtils.getFilterTerm(type).getBrand_id() == mBrand.getBrandId();
    int mTextColor;
    if (isChoose) {
      mTextColor = R.color.common_yellow;
      mHolder.brandChoose.setVisibility(View.VISIBLE);
    } else {
      mTextColor = R.color.common_text_black;
      mHolder.brandChoose.setVisibility(View.GONE);
    }
    int color = BeeUtils.getResColor(mTextColor);
    mHolder.brandName.setTextColor(color);
    mHolder.brandName.setText(mBrand.getBrandName());
    setUpBrand(mHolder, mBrand);
    setIndexer(mHolder, position, mBrand);
    if (position < getCount() - 1) {
      setLine(mHolder, position + 1);
    }
    mHolder.brandName.setTag(convert2BrandEntity(mBrand));
    mHolder.brandName.setOnClickListener(mClickListener);

    return convertView;
  }

  private BrandEntity convert2BrandEntity(Brand brand) {
    BrandEntity brandEntity = new BrandEntity();
    brandEntity.setBrand_id(brand.getBrandId());
    brandEntity.setBrand_name(brand.getBrandName());
    brandEntity.setSeries(brand.getSeries_ids());
    return brandEntity;
  }

  private class ViewHolder {
    LinearLayout topLinear;
    TextView brandName;
    TextView IndexLetter;
    ImageView brandChoose;
    View line;
  }

  private void setLine(ViewHolder holder, int position) {
    int section = getSectionForPosition(position);
    if (position == getPositionForSection(section)) {
      holder.line.setVisibility(View.GONE);
    } else {
      holder.line.setVisibility(View.VISIBLE);
    }
  }

  private void setIndexer(ViewHolder holder, int position, Brand brand) {
    int section = getSectionForPosition(position);
    if (position == getPositionForSection(section)) {
      holder.topLinear.setVisibility(View.VISIBLE);
      holder.IndexLetter.setText(brand.getSortLetter());
    } else {
      holder.topLinear.setVisibility(View.GONE);
    }
  }

  private void setUpBrand(ViewHolder holder, Brand brand) {
    int resID = ViewUtils.getBrandResID(brand.getBrandId());
    ViewUtils.setTextViewDrawable(holder.brandName, resID, BeeConstants.DRAWABLE_LEFT, 7 / 10F);
  }

  /** 获取当前ascii值首次出现的位置 */
  public int getPositionForSection(int sectionIndex) {
    for (int i = 0; i < getCount(); i++) {
      char sortChar = brands.get(i).getSortLetter().toUpperCase().charAt(0);
      if (sortChar == sectionIndex) {
        return i;
      }
    }
    return -1;
  }

  /** 根据ListView的当前位置获取分类的首字母的Char ascii值 */
  private int getSectionForPosition(int position) {
    return brands.get(position).getSortLetter().charAt(0);
  }
}
