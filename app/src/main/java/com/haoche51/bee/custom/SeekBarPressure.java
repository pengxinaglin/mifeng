package com.haoche51.bee.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.haoche51.bee.R;
import com.haoche51.bee.util.BeeUtils;
import com.haoche51.bee.util.BeeConstants;
import java.math.BigDecimal;

/**
 * 如果要设置总刻度，就要去设置后滑块位置。并且不能超过总刻度
 * 如果不设置，那么第一次绘制时，后滑块就会按照默认值进行绘制。
 *
 * 布局中一定要用match_parent.想设置距离其他控件的距离，可以使用padding。
 * 宽度要这样设置，高度不用设置(设置多少都没用，在这里已经写死了)，
 */
public class SeekBarPressure extends View {

  private static final int CLICK_ON_LOW = 1;      //点击在前滑块上
  private static final int CLICK_ON_HIGH = 2;     //点击在后滑块上
  private static final int CLICK_IN_LOW_AREA = 3;
  private static final int CLICK_IN_HIGH_AREA = 4;
  private static final int CLICK_OUT_AREA = 5;
  private static final int CLICK_INVALID = 0;

  private static final int[] STATE_NORMAL = {};
  private static final int[] STATE_PRESSED = {
      android.R.attr.state_pressed, android.R.attr.state_window_focused,
  };
  private Drawable hasScrollBarBg;        //滑动条滑动后背景图
  private Drawable notScrollBarBg;        //滑动条未滑动背景图
  private Drawable mThumbLow;         //前滑块
  private Drawable mThumbHigh;        //后滑块

  private int mScrollBarWidth;     //控件宽度=滑动条宽度+滑动块宽度
  private int mScrollBarHeight;    //滑动条高度

  private int mThumbWidth;        //滑动块宽度
  private int mThumbHeight;       //滑动块高度

  private double mOffsetLow = 0;     //前滑块X轴中心坐标
  private double mOffsetHigh = 0;    //后滑块X轴中心坐标
  private int mDistance = 0;      //总刻度
  private int mThumbMarginTop = 10;   //滑动块顶部距离上边框距离
  private int mThumbMarginBottom = 50;   //滑动块底部距离下边框距离

  private int mFlag = CLICK_INVALID;
  private OnSeekBarChangeListener mBarChangeListener;

  private double defaultScreenLow = 0;    //默认前滑块位置的比例
  private double defaultScreenHigh = 100;  //默认后滑块位置的比例（defaultScreenHigh/defaultScale）

  private boolean isEdit = false;     //输入框是否正在输入

  private int defaultScale = 100;     //默认总刻度

  private int paddingLeft = 0;
  private int paddingRight = 0;

  private int type;
  public static final int PRICE = 0x120701;
  public static final int DISTANCE = 0x120702;
  public static final int AGE = 0x120703;

  private Paint text_Paint = new Paint();
  private int mBlackColor = BeeUtils.getResColor(R.color.common_text_gray);

  //设置总刻度
  public void setDefaultScale(int defaultScale) {
    this.defaultScale = defaultScale;
  }

  public SeekBarPressure(Context context) {
    this(context, null);
  }

  public SeekBarPressure(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SeekBarPressure(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    Resources resources = getResources();
    notScrollBarBg = resources.getDrawable(R.drawable.filter_line_gray);
    hasScrollBarBg = resources.getDrawable(R.drawable.filter_line_yellow);
    mThumbLow = resources.getDrawable(R.drawable.icon_filter_seekbar);
    mThumbHigh = resources.getDrawable(R.drawable.icon_filter_seekbar);
    if (notScrollBarBg == null || hasScrollBarBg == null || mThumbLow == null
        || mThumbHigh == null) {
      throw new RuntimeException("no resources for drawable");
    }

    mThumbLow.setState(STATE_NORMAL);
    mThumbHigh.setState(STATE_NORMAL);

    paddingLeft = getPaddingLeft();
    paddingRight = getPaddingRight();

    mScrollBarWidth = notScrollBarBg.getIntrinsicWidth();
    mScrollBarHeight = notScrollBarBg.getIntrinsicHeight();

    mThumbWidth = mThumbLow.getIntrinsicWidth();
    mThumbHeight = mThumbLow.getIntrinsicHeight();
    text_Paint.setTextAlign(Paint.Align.CENTER);
  }

  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = MeasureSpec.getSize(widthMeasureSpec);
    mScrollBarWidth = width - mThumbWidth - paddingLeft - paddingRight;
    mOffsetLow = mThumbWidth / 2 + paddingLeft;
    mOffsetHigh = mScrollBarWidth + mOffsetLow;
    mDistance = mScrollBarWidth;

    mOffsetLow =
        formatDouble(defaultScreenLow / defaultScale * (mDistance)) + mThumbWidth / 2 + paddingLeft;
    mOffsetHigh = formatDouble(defaultScreenHigh / defaultScale * (mDistance)) + mThumbWidth / 2
        + paddingLeft;
    setMeasuredDimension(width, mThumbHeight + mThumbMarginTop + mThumbMarginBottom);
  }

  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    int scrollBarTop = mThumbMarginTop + (mThumbHeight - mScrollBarHeight) / 2;
    int scrollBarBottom = mThumbMarginTop + (mThumbHeight + mScrollBarHeight) / 2;

    //白色，不会动
    notScrollBarBg.setBounds(mThumbWidth / 2 + paddingLeft, scrollBarTop,
        mScrollBarWidth + mThumbWidth / 2 + paddingLeft, scrollBarBottom);
    notScrollBarBg.draw(canvas);

    //红色，中间部分会动
    hasScrollBarBg.setBounds((int) mOffsetLow, scrollBarTop, (int) mOffsetHigh, scrollBarBottom);
    hasScrollBarBg.draw(canvas);

    //前滑块
    mThumbLow.setBounds((int) (mOffsetLow - mThumbWidth / 2), mThumbMarginTop,
        (int) (mOffsetLow + mThumbWidth / 2), mThumbHeight + mThumbMarginTop);
    mThumbLow.draw(canvas);

    //后滑块
    mThumbHigh.setBounds((int) (mOffsetHigh - mThumbWidth / 2), mThumbMarginTop,
        (int) (mOffsetHigh + mThumbWidth / 2), mThumbHeight + mThumbMarginTop);
    mThumbHigh.draw(canvas);

    /** 最关键的地方，计算前后的值 */
    double progressLow =
        formatDouble((mOffsetLow - mThumbWidth / 2 - paddingLeft) * defaultScale / mDistance);
    double progressHigh =
        formatDouble((mOffsetHigh - mThumbWidth / 2 - paddingLeft) * defaultScale / mDistance);

    drawScale(type, canvas);

    if (mBarChangeListener != null) {
      if (!isEdit) {
        mBarChangeListener.onProgressChanged(this, progressLow, progressHigh);
      }
    }
  }

  private void drawScale(int type, Canvas canvas) {
    text_Paint.setColor(mBlackColor);
    text_Paint.setTextSize(30);
    int drawHeight = mThumbMarginTop + mThumbHeight + 30;
    switch (type) {
      case PRICE:
        canvas.drawText("0", paddingLeft + mThumbWidth / 2, drawHeight, text_Paint);
        canvas.drawText("10", paddingLeft + mThumbWidth / 2 + mDistance * 10 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText("20", paddingLeft + mThumbWidth / 2 + mDistance * 20 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText("30", paddingLeft + mThumbWidth / 2 + mDistance * 30 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText("40", paddingLeft + mThumbWidth / 2 + mDistance * 40 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText(BeeConstants.UNLIMITED, paddingLeft + mThumbWidth / 2 + mDistance,
            drawHeight, text_Paint);
        break;
      case DISTANCE:
        canvas.drawText("0", paddingLeft + mThumbWidth / 2, drawHeight, text_Paint);
        canvas.drawText("3", paddingLeft + mThumbWidth / 2 + mDistance * 3 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText("6", paddingLeft + mThumbWidth / 2 + mDistance * 6 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText("9", paddingLeft + mThumbWidth / 2 + mDistance * 9 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText("12", paddingLeft + mThumbWidth / 2 + mDistance * 12 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText("15", paddingLeft + mThumbWidth / 2 + mDistance * 15 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText(BeeConstants.UNLIMITED, paddingLeft + mThumbWidth / 2 + mDistance,
            drawHeight, text_Paint);
        break;
      case AGE:
        canvas.drawText("0", paddingLeft + mThumbWidth / 2, drawHeight, text_Paint);
        canvas.drawText("2", paddingLeft + mThumbWidth / 2 + mDistance * 2 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText("4", paddingLeft + mThumbWidth / 2 + mDistance * 4 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText("6", paddingLeft + mThumbWidth / 2 + mDistance * 6 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText("8", paddingLeft + mThumbWidth / 2 + mDistance * 8 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText("10", paddingLeft + mThumbWidth / 2 + mDistance * 10 / defaultScale,
            drawHeight, text_Paint);
        canvas.drawText(BeeConstants.UNLIMITED, paddingLeft + mThumbWidth / 2 + mDistance,
            drawHeight, text_Paint);
        break;
    }
  }

  @Override public boolean onTouchEvent(MotionEvent e) {
    if (e.getAction() == MotionEvent.ACTION_DOWN) {
      if (mBarChangeListener != null) {
        mBarChangeListener.onProgressBefore();
        isEdit = false;
      }
      mFlag = getAreaFlag(e);
      if (mFlag == CLICK_ON_LOW) {
        mThumbLow.setState(STATE_PRESSED);
      } else if (mFlag == CLICK_ON_HIGH) {
        mThumbHigh.setState(STATE_PRESSED);
      } else if (mFlag == CLICK_IN_LOW_AREA) {
        mThumbLow.setState(STATE_PRESSED);
        //如果点击0到mThumbWidth /2 + paddingLeft坐标
        if (e.getX() < 0 || e.getX() <= mThumbWidth / 2 + paddingLeft) {
          mOffsetLow = mThumbWidth / 2 + paddingLeft;
        } else {
          mOffsetLow = formatDouble(e.getX());
        }
      } else if (mFlag == CLICK_IN_HIGH_AREA) {
        mThumbHigh.setState(STATE_PRESSED);
        if (e.getX() >= mScrollBarWidth + mThumbWidth / 2 + paddingLeft) {
          mOffsetHigh = mDistance + mThumbWidth / 2 + paddingLeft;
        } else {
          mOffsetHigh = formatDouble(e.getX());
        }
      }
      refresh();
    } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
      if (mFlag == CLICK_ON_LOW) {
        //点击在前滑块上移动时的情况
        if (e.getX() < 0 || e.getX() <= mThumbWidth / 2 + paddingLeft) {
          mOffsetLow = mThumbWidth / 2 + paddingLeft;
        } else if (e.getX()
            >= mScrollBarWidth + paddingLeft + mThumbWidth / 2 - mDistance / defaultScale) {
          mOffsetHigh = paddingLeft + mDistance + mThumbWidth / 2;
          mOffsetLow = mOffsetHigh - mDistance / defaultScale + 2;
        } else {
          mOffsetLow = formatDouble(e.getX());
          if (mOffsetHigh - mOffsetLow <= mDistance / defaultScale) {
            mOffsetHigh =
                (mOffsetLow <= paddingLeft + mThumbWidth / 2 + mDistance - mDistance / defaultScale)
                    ? mOffsetLow + mDistance / defaultScale
                    : paddingLeft + mDistance + mThumbWidth / 2;
          }
        }
      } else if (mFlag == CLICK_ON_HIGH) {
        //点击在后滑块上移动时的情况
        if (e.getX() < mThumbWidth / 2 + paddingLeft + mDistance / defaultScale) {
          mOffsetLow = mThumbWidth / 2 + paddingLeft;
          mOffsetHigh = mOffsetLow + mDistance / defaultScale + 2;
        } else if (e.getX() > mScrollBarWidth + paddingLeft + mThumbWidth / 2) {
          mOffsetHigh = mThumbWidth / 2 + paddingLeft + mDistance;
        } else {
          mOffsetHigh = formatDouble(e.getX());
          if (mOffsetHigh - mOffsetLow <= mDistance / defaultScale) {
            mOffsetLow =
                (mOffsetHigh >= mThumbWidth / 2 + mDistance / defaultScale + paddingLeft) ? (
                    mOffsetHigh - mDistance / defaultScale) : mThumbWidth / 2 + paddingLeft;
          }
        }
      }
      refresh();
    } else if (e.getAction() == MotionEvent.ACTION_UP) {
      mThumbLow.setState(STATE_NORMAL);
      mThumbHigh.setState(STATE_NORMAL);

      if (mBarChangeListener != null) {
        mBarChangeListener.onProgressAfter();
      }
    }
    return true;
  }

  //获取手指点击位置对应的响应
  public int getAreaFlag(MotionEvent e) {
    int top = 0;
    int bottom = mThumbHeight + mThumbMarginTop + 20;
    /** 左右个扩大半个滑块的点击区间 */
    if (e.getY() >= top && e.getY() <= bottom && e.getX() >= (mOffsetLow - mThumbWidth)
        && e.getX() <= mOffsetLow + mThumbWidth) {
      return CLICK_ON_LOW;
    } else if (e.getY() >= top && e.getY() <= bottom && e.getX() >= (mOffsetHigh - mThumbWidth)
        && e.getX() <= (mOffsetHigh + mThumbWidth)) {
      return CLICK_ON_HIGH;
    } else if (e.getY() >= top && e.getY() <= bottom && (
        (e.getX() >= 0 && e.getX() < (mOffsetLow - mThumbWidth / 2)) || (
            (e.getX() > (mOffsetLow + mThumbWidth / 2))
                && e.getX() <= (mOffsetHigh + mOffsetLow) / 2))) {
      return CLICK_IN_LOW_AREA;
    } else if (e.getY() >= top && e.getY() <= bottom && (
        ((e.getX() > (mOffsetHigh + mOffsetLow) / 2) && e.getX() < (mOffsetHigh - mThumbWidth / 2))
            || (e.getX() > (mOffsetHigh + mThumbWidth / 2)
            && e.getX() <= mScrollBarWidth + mThumbWidth + paddingLeft + paddingRight))) {
      return CLICK_IN_HIGH_AREA;
    } else if (!(e.getX() >= 0
        && e.getX() <= mScrollBarWidth + paddingLeft + paddingRight + mThumbWidth && e.getY() >= top
        && e.getY() <= bottom)) {
      return CLICK_OUT_AREA;
    } else {
      return CLICK_INVALID;
    }
  }

  //更新滑块
  private void refresh() {
    invalidate();
  }

  //设置前滑块的值
  public void setProgressLow(double progressLow) {
    this.defaultScreenLow = progressLow;
    mOffsetLow =
        formatDouble(progressLow / defaultScale * (mDistance)) + mThumbWidth / 2 + paddingLeft;
    isEdit = true;
    refresh();
  }

  //设置后滑块的值
  public void setProgressHigh(double progressHigh) {
    this.defaultScreenHigh = progressHigh;
    mOffsetHigh =
        formatDouble(progressHigh / defaultScale * (mDistance)) + mThumbWidth / 2 + paddingLeft;
    isEdit = true;
    refresh();
  }

  public void setType(int type) {
    this.type = type;
    refresh();
  }

  public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener) {
    this.mBarChangeListener = mListener;
  }

  //回调函数，在滑动时实时调用，改变输入框的值
  public interface OnSeekBarChangeListener {
    //滑动前
    void onProgressBefore();

    //滑动时
    void onProgressChanged(SeekBarPressure seekBar, double progressLow, double progressHigh);

    //滑动后
    void onProgressAfter();
  }

  public static double formatDouble(double pDouble) {
    BigDecimal bd = new BigDecimal(pDouble);
    BigDecimal bd1 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
    pDouble = bd1.doubleValue();
    return pDouble;
  }
}
