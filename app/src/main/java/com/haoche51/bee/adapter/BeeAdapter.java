package com.haoche51.bee.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public abstract class BeeAdapter<T> extends android.widget.BaseAdapter {
  protected Context mContext;
  protected List<T> mBaseDatas;
  protected int mLayoutId;

  public BeeAdapter(Context context, List<T> data, int layoutId) {
    this.mContext = context;
    this.mBaseDatas = data;
    this.mLayoutId = layoutId;
  }

  public List<T> getBaseData() {
    return mBaseDatas;
  }

  @Override public int getCount() {
    if (mBaseDatas == null) {
      return 0;
    }
    return mBaseDatas.size();
  }

  @Override public T getItem(int position) {
    if (position > mBaseDatas.size() - 1) {
      return mBaseDatas.get(mBaseDatas.size() - 1);
    }
    return mBaseDatas.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    BeeViewHolder holder =
        BeeViewHolder.getInstance(mContext, convertView, parent, mLayoutId, position);
    fillViewData(holder, position);
    return holder.getConvertView();
  }

  public abstract void fillViewData(BeeViewHolder holder, int position);
}
