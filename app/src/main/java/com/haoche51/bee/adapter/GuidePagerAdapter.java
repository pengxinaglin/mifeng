package com.haoche51.bee.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.List;

public class GuidePagerAdapter extends PagerAdapter {
  private List<ImageView> imageViews;

  public GuidePagerAdapter(List<ImageView> imageViews) {
    this.imageViews = imageViews;
  }

  @Override public int getCount() {
    if (imageViews != null) return imageViews.size();
    return 0;
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {
    container.addView(imageViews.get(position));
    return imageViews.get(position);
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView(imageViews.get(position));
  }
}
