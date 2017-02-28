package com.haoche51.bee.util;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import com.haoche51.bee.R;

public class ViewUtils {

  /***
   * 事件约定本地的品牌icon格式为: b品牌id.如(b110.png)
   * 根据传入的品牌id获取相应品牌icon
   * 如果找不到返回empty_brand
   */
  public static int getBrandResID(int brand_id) {
    final String packageName = BeeUtils.getPackageName();
    Resources mRes = BeeUtils.getResources();
    int brandIcon = mRes.getIdentifier(String.valueOf("b" + brand_id), "drawable", packageName);
    return brandIcon > 0 ? brandIcon : R.drawable.empty_brand;
  }

  public static int getCarTypeResID(int id) {
    final String packageName = BeeUtils.getPackageName();
    Resources mRes = BeeUtils.getResources();
    int carTypeIcon =
        mRes.getIdentifier(String.valueOf("filter_stru" + id), "drawable", packageName);
    return carTypeIcon > 0 ? carTypeIcon : 0;
  }

  public static int getPlatformResID(int id) {
    final String packageName = BeeUtils.getPackageName();
    Resources mRes = BeeUtils.getResources();
    int platformIcon = mRes.getIdentifier(String.valueOf("platform" + id), "drawable", packageName);
    return platformIcon > 0 ? platformIcon : R.drawable.empty_brand;
  }

  public static void animateLayout(final View view, int from, int to) {
    if (view == null) return;
    final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    ValueAnimator animator = ValueAnimator.ofInt(from, to);
    animator.setDuration(1000);
    animator.setInterpolator(new LinearInterpolator());
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        lp.topMargin = (int) animation.getAnimatedValue();
        lp.bottomMargin = -(int) animation.getAnimatedValue();
        view.requestLayout();
      }
    });
    animator.start();
  }

  /** 设置tv字体颜色为指定color,从from到to */
  public static void changeTextViewColor(TextView tv, int color, int... scope) {
    if (tv == null) return;
    String str = tv.getText().toString();
    if (TextUtils.isEmpty(str)) return;

    int len = str.length();
    int from = 0;
    int to = 0;
    if (scope == null) {
      from = 0;
      to = len;
    } else {
      if (scope.length == 1) {
        from = scope[0];
        to = len;
      }
      if (scope.length == 2) {
        from = scope[0];
        to = scope[1];
      }
    }

    SpannableStringBuilder spb = new SpannableStringBuilder(str);
    spb.setSpan(new ForegroundColorSpan(color), from, to,
        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    tv.setText(spb);
  }

  /**
   * 注意：如果要设置drawablePadding，那么就要在调用该方法前调用
   * tv.setCompoundDrawablePadding(padding);
   * 默认使用的是布局文件中设置的drawablePadding。
   */
  public static void setTextViewDrawable(TextView tv, int resDrawableID, int type, float scale) {
    float f = BeeUtils.getScreenWidthInPixels() / 750F;
    Drawable logo = null;
    try {
      logo = BeeUtils.getResDrawable(resDrawableID);
    } catch (Exception e) {
      //
    }
    if (logo != null && tv != null) {
      logo.setBounds(0, 0, (int) (logo.getIntrinsicWidth() * scale * f),
          (int) (logo.getIntrinsicHeight() * scale * f));
      switch (type) {
        case BeeConstants.DRAWABLE_LEFT:
          tv.setCompoundDrawables(logo, null, null, null);
          break;
        case BeeConstants.DRAWABLE_TOP:
          tv.setCompoundDrawables(null, logo, null, null);
          break;
        case BeeConstants.DRAWABLE_RIGHT:
          tv.setCompoundDrawables(null, null, logo, null);
          break;
        case BeeConstants.DRAWABLE_BOTTOM:
          tv.setCompoundDrawables(null, null, null, logo);
          break;
      }
    }
  }
}
