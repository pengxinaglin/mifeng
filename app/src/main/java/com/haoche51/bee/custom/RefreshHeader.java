package com.haoche51.bee.custom;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.bee.R;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class RefreshHeader extends FrameLayout implements PtrUIHandler {
  private TextView mTitleTextView;
  private ImageView mLoadingIv;

  public RefreshHeader(Context context) {
    super(context);
    initViews();
  }

  public RefreshHeader(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViews();
  }

  public RefreshHeader(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initViews();
  }

  protected void initViews() {
    int resLayout = R.layout.custom_refresh_header;
    View header = LayoutInflater.from(getContext()).inflate(resLayout, this);
    mLoadingIv = (ImageView) header.findViewById(R.id.iv_refresh);
    mTitleTextView = (TextView) header.findViewById(R.id.tv_refresh);
  }

  @Override public void onUIReset(PtrFrameLayout frame) {
    mTitleTextView.setVisibility(VISIBLE);
  }

  @Override public void onUIRefreshPrepare(PtrFrameLayout frame) {
    if (!frame.isPullToRefresh()) {
      mTitleTextView.setText(getResources().getString(R.string.bee_pull_down));
    }
  }

  @Override public void onUIRefreshBegin(PtrFrameLayout frame) {

    Drawable drawable = mLoadingIv.getBackground();
    if (drawable instanceof AnimationDrawable) {
      AnimationDrawable anim = (AnimationDrawable) drawable;
      anim.start();
    }
    mTitleTextView.setVisibility(GONE);
  }

  @Override public void onUIRefreshComplete(PtrFrameLayout frame) {

    Drawable drawable = mLoadingIv.getBackground();
    if (drawable instanceof AnimationDrawable) {
      AnimationDrawable anim = (AnimationDrawable) drawable;
      anim.stop();
    }
  }

  @Override public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status,
      PtrIndicator ptrIndicator) {

    final int mOffsetToRefresh = frame.getOffsetToRefresh();
    final int currentPos = ptrIndicator.getCurrentPosY();
    final int lastPos = ptrIndicator.getLastPosY();

    if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
      if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
        crossRotateLineFromBottomUnderTouch(frame);
      }
    } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
      if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
        crossRotateLineFromTopUnderTouch(frame);
      }
    }
  }

  private void crossRotateLineFromTopUnderTouch(PtrFrameLayout frame) {
    if (!frame.isPullToRefresh()) {
      mTitleTextView.setText(R.string.bee_release_to_refresh);
    }
  }

  private void crossRotateLineFromBottomUnderTouch(PtrFrameLayout frame) {
    if (!frame.isPullToRefresh()) {
      mTitleTextView.setText(getResources().getString(R.string.bee_pull_down));
    }
  }
}
