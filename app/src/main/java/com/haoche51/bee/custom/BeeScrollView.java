package com.haoche51.bee.custom;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class BeeScrollView extends ScrollView {

  private ScrollViewListener scrollViewListener = null;

  public BeeScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public BeeScrollView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BeeScrollView(Context context) {
    this(context, null, 0);
  }

  @Override protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
    //		return super.computeScrollDeltaToGetChildRectOnScreen(rect);
    return 0;
  }

  public void setScrollViewListener(ScrollViewListener scrollViewListener) {
    this.scrollViewListener = scrollViewListener;
  }

  @Override protected void onScrollChanged(int x, int y, int oldX, int oldY) {
    super.onScrollChanged(x, y, oldX, oldY);
    if (scrollViewListener != null) {
      scrollViewListener.onScrollChanged(this, x, y, oldX, oldY);
    }
  }

  // 滑动距离及坐标
  private float xDistance, yDistance, xLast, yLast;

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        xDistance = yDistance = 0f;
        xLast = ev.getX();
        yLast = ev.getY();
        break;
      case MotionEvent.ACTION_MOVE:
        final float curX = ev.getX();
        final float curY = ev.getY();

        xDistance += Math.abs(curX - xLast);
        yDistance += Math.abs(curY - yLast);
        xLast = curX;
        yLast = curY;

        if (xDistance > yDistance) {
          return false; // 表示向下传递事件
        }
    }

    return super.onInterceptTouchEvent(ev);
  }

  public interface ScrollViewListener {
    void onScrollChanged(BeeScrollView scrollView, int x, int y, int oldX, int oldY);
  }
}
