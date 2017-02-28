package com.haoche51.bee.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.haoche51.bee.GlobalData;
import com.haoche51.bee.R;
import com.haoche51.bee.activity.VehicleDetailActivity;
import com.haoche51.bee.entity.VehicleItemEntity;
import com.haoche51.bee.util.BeeStatistics;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.FormatUtils;
import java.util.List;

public class SinglePicVehicleAdapter extends BeeAdapter<VehicleItemEntity> {

  public SinglePicVehicleAdapter(Context context, List<VehicleItemEntity> data, int layoutId) {
    super(context, data, layoutId);
  }

  public void fillOtherData(BeeViewHolder holder, int position) {
  }

  @Override public void fillViewData(BeeViewHolder holder, int position) {
    final VehicleItemEntity entity = getItem(position);
    if (entity != null) {
      //设置名字
      String vehicleName = FormatUtils.getVehicleName(entity.getTitle());
      TextView mNameTv = holder.findTheView(R.id.tv_single_pic_vehicle_name);
      mNameTv.setText(vehicleName);

      //设置detail
      String time = entity.getRegister_time();
      String mile = entity.getMiles();
      String gearbox = entity.getGearbox();
      String detail = FormatUtils.getVehicleFormat(time, mile, gearbox);
      holder.setTextViewText(R.id.tv_single_pic_vehicle_detail, detail);

      // 设置价格
      float price = BeeUtils.str2Float(entity.getSell_price());
      Spanned spannedPrice = Html.fromHtml(FormatUtils.getSoldPriceFormat(price));
      holder.setTextViewText(R.id.tv_single_pic_vehicle_price, spannedPrice);

      if (!BeeUtils.isListEmpty(entity.getPlatform()) && entity.getPlatform().size() > 1) {
        holder.setTextViewText(R.id.tv_single_pic_vehicle_price_hint,
            BeeUtils.getResString(R.string.vehicle_list_price));
      } else {
        holder.setTextViewText(R.id.tv_single_pic_vehicle_price_hint,
            BeeUtils.getResString(R.string.vehicle_list_price_wan));
      }

      // 降价
      String cp = entity.getCut_price();
      TextView cpTv = holder.findTheView(R.id.tv_single_pic_vehicle_cut_price);
      if (TextUtils.isEmpty(cp) || BeeUtils.str2Int(cp) == 0) {
        cpTv.setVisibility(View.GONE);
      } else {
        float cutPrice = BeeUtils.str2Float(cp);
        Spanned spannedCutPrice = Html.fromHtml(FormatUtils.getSoldPriceFormat(cutPrice));
        String s = BeeUtils.getResString(R.string.vehicle_list_cut_price, spannedCutPrice);
        cpTv.setText(s);
        cpTv.setVisibility(View.VISIBLE);
      }
      //设置图片
      String picUrl = entity.getImg_url();
      if (!TextUtils.isEmpty(picUrl)) {
        holder.loadHttpImage(R.id.iv_single_pic_vehicle_image, picUrl);
      }

      //设置平台
      TextView p1 = holder.findTheView(R.id.tv_single_pic_vehicle_platform_one);
      TextView p2 = holder.findTheView(R.id.tv_single_pic_vehicle_platform_two);
      TextView p3 = holder.findTheView(R.id.tv_single_pic_vehicle_platform_three);
      TextView pAll = holder.findTheView(R.id.tv_single_pic_vehicle_platform_all);
      List<String> platform = entity.getPlatform();
      setPlatform(platform, p1, p2, p3, pAll);

      //点击进入详情页
      View innerView = holder.findTheView(R.id.rel_single_pic_parent);
      if (innerView != null) {
        innerView.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            VehicleDetailActivity.idToThis(GlobalData.mContext, entity.getId());
            BeeStatistics.VehicleDetailClick(entity.getId());
          }
        });
      }
    }
    fillOtherData(holder, position);
  }

  private void setPlatform(List<String> platform, TextView p1, TextView p2, TextView p3,
      TextView pAll) {
    if (BeeUtils.isListEmpty(platform)) return;
    switch (platform.size()) {
      case 1:
        p1.setText(platform.get(0));
        p1.setVisibility(View.VISIBLE);
        p2.setVisibility(View.GONE);
        p3.setVisibility(View.GONE);
        pAll.setVisibility(View.GONE);
        break;
      case 2:
        p1.setText(platform.get(0));
        p2.setText(platform.get(1));
        p1.setVisibility(View.VISIBLE);
        p2.setVisibility(View.VISIBLE);
        p3.setVisibility(View.GONE);
        pAll.setVisibility(View.GONE);
        break;
      case 3:
        p1.setText(platform.get(0));
        p2.setText(platform.get(1));
        p3.setText(platform.get(2));
        p1.setVisibility(View.VISIBLE);
        p2.setVisibility(View.VISIBLE);
        p3.setVisibility(View.VISIBLE);
        pAll.setVisibility(View.GONE);
        break;
      default:
        p1.setText(platform.get(0));
        p2.setText(platform.get(1));
        p3.setText(platform.get(2));
        p1.setVisibility(View.VISIBLE);
        p2.setVisibility(View.VISIBLE);
        p3.setVisibility(View.VISIBLE);
        pAll.setVisibility(View.VISIBLE);
        break;
    }
  }
}
