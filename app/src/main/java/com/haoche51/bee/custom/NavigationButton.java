package com.haoche51.bee.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RadioButton;
import com.haoche51.bee.R;

public class NavigationButton extends RadioButton {

  private boolean reDraw = false;
  private int radius;
  private Paint mPaint;

  public NavigationButton(Context context) {
    super(context);
    init();
  }

  public NavigationButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public NavigationButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    mPaint = new Paint();
    mPaint.setColor(Color.RED);
    radius = getResources().getDimensionPixelSize(R.dimen.px_5dp);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (reDraw) {
      canvas.drawCircle(getWidth() / 2 + 3 * radius, radius, radius, mPaint);
    }
  }

  public void hideIndicator() {
    reDraw = false;
    invalidate();
  }

  public void showIndicator() {
    reDraw = true;
    invalidate();
  }
}
