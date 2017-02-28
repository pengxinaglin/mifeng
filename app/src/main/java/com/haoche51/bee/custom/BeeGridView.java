package com.haoche51.bee.custom;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class BeeGridView extends GridView {
    public BeeGridView(Context context) {
        super(context);
    }

    public BeeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
