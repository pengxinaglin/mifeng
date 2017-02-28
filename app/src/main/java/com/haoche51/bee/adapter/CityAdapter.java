package com.haoche51.bee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.haoche51.bee.GlobalData;
import com.haoche51.bee.R;
import com.haoche51.bee.entity.CityEntity;
import java.util.List;

public class CityAdapter extends BaseAdapter {
  private List<CityEntity> cityEntities;

  public CityAdapter(List<CityEntity> cityEntities, View.OnClickListener listener) {
    this.cityEntities = cityEntities;
    this.mClickListener = listener;
  }

  private View.OnClickListener mClickListener;

  @Override public int getCount() {
    return cityEntities.size();
  }

  @Override public Object getItem(int position) {
    return cityEntities.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder mHolder;
    if (convertView == null) {
      mHolder = new ViewHolder();
      int resLayout = R.layout.lvitem_city;
      convertView = LayoutInflater.from(GlobalData.mContext).inflate(resLayout, parent, false);
      mHolder.topLinear = (LinearLayout) convertView.findViewById(R.id.linear_city_item);
      mHolder.cityName = (TextView) convertView.findViewById(R.id.city_name);
      mHolder.IndexLetter = (TextView) convertView.findViewById(R.id.city_letter);
      mHolder.line = convertView.findViewById(R.id.city_line);
      convertView.setTag(mHolder);
      mHolder.IndexLetter.getPaint().setFakeBoldText(true);
    } else {
      mHolder = (ViewHolder) convertView.getTag();
    }

    CityEntity cityEntity = cityEntities.get(position);
    mHolder.cityName.setText(cityEntity.getCity_name());
    setIndexer(mHolder, position, cityEntity);
    if (position < getCount() - 1) {
      setLine(mHolder, position + 1);
    }
    mHolder.cityName.setTag(cityEntity);
    mHolder.cityName.setOnClickListener(mClickListener);

    return convertView;
  }

  private class ViewHolder {
    LinearLayout topLinear;
    TextView cityName;
    TextView IndexLetter;
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

  private void setIndexer(ViewHolder holder, int position, CityEntity cityEntity) {
    int section = getSectionForPosition(position);
    if (position == getPositionForSection(section)) {
      holder.topLinear.setVisibility(View.VISIBLE);
      holder.IndexLetter.setText(cityEntity.getFirst_char().toUpperCase());
    } else {
      holder.topLinear.setVisibility(View.GONE);
    }
  }

  /** 获取当前ascii值首次出现的位置 */
  public int getPositionForSection(int sectionIndex) {
    for (int i = 0; i < getCount(); i++) {
      char sortChar = cityEntities.get(i).getFirst_char().toUpperCase().charAt(0);
      if (sortChar == sectionIndex) {
        return i;
      }
    }
    return -1;
  }

  /** 根据ListView的当前位置获取分类的首字母的Char ascii值 */
  private int getSectionForPosition(int position) {
    return cityEntities.get(position).getFirst_char().toUpperCase().charAt(0);
  }
}
