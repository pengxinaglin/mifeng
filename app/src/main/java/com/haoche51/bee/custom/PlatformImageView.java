package com.haoche51.bee.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/** 首先需要确认每个平台中心点的坐标,这样才可以精确的计算每一个点击区域 */
public class PlatformImageView extends ImageView {

  private float lineLength;

  private float[] arrayCenterX;
  private float[] arrayCenterY;

  public PlatformImageView(Context context) {
    super(context);
  }

  public PlatformImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PlatformImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    initData();
  }

  private void initData() {
    int sw = getWidth();
    int sh = getHeight();
    float centerX = sw / 2F;
    float centerY = sh / 2F;
    float paddingWidth = sw * (60 / 1040F);
    lineLength = (sw * (980 / 1040F)) / (3 * (float) Math.sqrt((3)));
    arrayCenterX = new float[] {
        centerX - lineLength * (float) Math.sqrt(3) / 2 - paddingWidth / 2,
        centerX + lineLength * (float) Math.sqrt(3) / 2 + paddingWidth / 2,
        centerX + lineLength * (float) Math.sqrt(3) + paddingWidth,
        centerX + lineLength * (float) Math.sqrt(3) / 2 + paddingWidth / 2,
        centerX - lineLength * (float) Math.sqrt(3) / 2 - paddingWidth / 2,
        centerX - lineLength * (float) Math.sqrt(3) - paddingWidth, centerX
    };
    arrayCenterY = new float[] {
        centerY - 3 * lineLength / 2 - paddingWidth, centerY - 3 * lineLength / 2 - paddingWidth,
        centerY, centerY + 3 * lineLength / 2 + paddingWidth,
        centerY + 3 * lineLength / 2 + paddingWidth, centerY, centerY
    };
  }

  private int curAction = -1;
  private ActionListener onActionListener;

  @Override public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        float touchedX = event.getX();
        float touchedY = event.getY();
        curAction = onTouchPosition(touchedX, touchedY);
        break;
      case MotionEvent.ACTION_UP:
        if (onActionListener != null) {
          onActionListener.actionListener(curAction);
        }
        curAction = -1;
        break;
    }
    return true;
  }

  public interface ActionListener {
    void actionListener(int actionIndex);
  }

  public void setOnActionListener(ActionListener onActionListener) {
    this.onActionListener = onActionListener;
  }

  private int onTouchPosition(float x, float y) {

    int curAction = -1;

    for (int index = 0; index < arrayCenterX.length; index++) {
      float curX = arrayCenterX[index];
      float curY = arrayCenterY[index];

      if (Math.pow(curX - x, 2) + Math.pow(curY - y, 2) <= 3 * Math.pow(lineLength, 2) / 4) {
        curAction = index;
        break;
      }
    }
    return curAction;
  }
}
