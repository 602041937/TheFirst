package com.hpd.thefirst;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class HPDLineaLayout extends ViewGroup {

    public HPDLineaLayout(Context context) {
        super(context);
    }

    public HPDLineaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HPDLineaLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HPDLineaLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int childCount = getChildCount();

        if (childCount == 0) {
            setMeasuredDimension(0, 0);
        } else {

            if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
                int height = getTotalHeight();
                int width = getMaxWidth();
                setMeasuredDimension(width, height);
            } else if (widthMode == MeasureSpec.AT_MOST) {
                int maxWidth = getMaxWidth();
                setMeasuredDimension(maxWidth, heightSize);
            } else if (heightMode == MeasureSpec.AT_MOST) {
                int height = getTotalHeight();
                setMeasuredDimension(widthSize, height);
            }
        }
    }


    private int getMaxWidth() {

        int maxWidth = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            int measuredWidth = getChildAt(i).getMeasuredWidth();
            if (measuredWidth > maxWidth) {
                maxWidth = measuredWidth;
            }
        }
        return maxWidth;
    }

    private int getTotalHeight() {

        int totalHeight = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            totalHeight += getChildAt(i).getMeasuredHeight();
        }
        return totalHeight;
    }

    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {

        int childCount = getChildCount();

        int currentHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(left, currentHeight, left + child.getMeasuredWidth(), currentHeight + child.getMeasuredHeight());
            currentHeight += child.getMeasuredHeight();
        }
    }
}
