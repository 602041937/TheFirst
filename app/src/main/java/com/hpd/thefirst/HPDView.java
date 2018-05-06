package com.hpd.thefirst;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class HPDView extends View {
    private static final String TAG = "HPDView";
    private Paint paint;

    @SuppressLint("ResourceAsColor")
    private void initViews(Context context) {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
    }

    public HPDView(Context context) {
        super(context);
        initViews(context);
    }

    public HPDView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public HPDView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    public HPDView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "width");
        int width = mySize(100, widthMeasureSpec);
        Log.i(TAG, "height");
        int height = mySize(100, heightMeasureSpec);

        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    private int mySize(int defaultSize, int measureSpec) {

        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.AT_MOST:
                mySize = size;
                Log.i(TAG, "mode: AT_MOST");
                Log.i(TAG, "size: " + size);
                break;

            case MeasureSpec.UNSPECIFIED:
                mySize = defaultSize;
                Log.i(TAG, "mode: UNSPECIFIED");
                Log.i(TAG, "size: " + size);
                break;

            case MeasureSpec.EXACTLY:
                mySize = size;
                Log.i(TAG, "mode: EXACTLY");
                Log.i(TAG, "size: " + size);
                break;
        }
        return mySize;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredHeight = getMeasuredHeight();
        int height = getHeight();
        Log.i(TAG, "measuredHeight: " + measuredHeight + ",height" + height);

        int measuredWidth = getMeasuredWidth();
        int width = getWidth();
        Log.i(TAG, "measuredWidth: " + measuredWidth + ",width" + width);


        canvas.drawCircle(measuredHeight / 4, measuredHeight / 4, 20, paint);
        canvas.drawCircle(measuredHeight / 4 * 3, measuredHeight / 4, 20, paint);
        canvas.drawCircle(measuredHeight / 4, measuredHeight / 4 * 3, 20, paint);
        canvas.drawCircle(measuredHeight / 4 * 3, measuredHeight / 4 * 3, 20, paint);
    }
}
