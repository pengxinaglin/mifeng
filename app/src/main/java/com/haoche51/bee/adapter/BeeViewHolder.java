package com.haoche51.bee.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.bee.helper.ImageLoaderHelper;

public class BeeViewHolder {
  private SparseArray<View> mViews;
  private int mPosition;
  private View mConvertView;

  public BeeViewHolder(Context context, ViewGroup parent, int layoutId, int mPosition) {
    this.mPosition = mPosition;
    this.mViews = new SparseArray<>();
    mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
    mConvertView.setTag(this);
  }

  public static BeeViewHolder getInstance(Context context, View convertView, ViewGroup parent,
      int layoutId, int position) {
    BeeViewHolder holder;
    if (convertView == null) {
      holder = new BeeViewHolder(context, parent, layoutId, position);
    } else {
      holder = (BeeViewHolder) convertView.getTag();
    }
    //复用的是convertView,position是不复用的
    holder.mPosition = position;
    return holder;
  }

  public View getConvertView() {
    return mConvertView;
  }

  public <T extends View> T findTheView(int viewId) {
    View view = mViews.get(viewId);
    if (view == null) {
      view = mConvertView.findViewById(viewId);
      mViews.put(viewId, view);
    }
    return (T) view;
  }

  public int getPosition() {
    return mPosition;
  }

  public void setTextViewText(int viewId, CharSequence text) {
    TextView tv = findTheView(viewId);
    tv.setText(text);
  }

  public void loadHttpImage(int viewId, String url) {
    ImageView iv = findTheView(viewId);
    ImageLoaderHelper.displayNormalImage(url, iv);
  }

  public void loadHttpImage(ImageView iv, String url) {
    ImageLoaderHelper.displayNormalImage(url, iv);
  }
}
