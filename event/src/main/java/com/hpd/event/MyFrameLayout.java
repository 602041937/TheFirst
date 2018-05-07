package com.hpd.event;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.EventLog;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MyFrameLayout extends FrameLayout {


    public MyFrameLayout(@NonNull Context context) {
        super(context);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean b = super.dispatchTouchEvent(ev);
        Log.i("dispatchTouchEvent", "MyFrameLayout: dispatchTouchEvent " + b);
        return b;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("onTouchEvent", "MyFrameLayout ACTION_DOWN: ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("onTouchEvent", "MyFrameLayout ACTION_MOVE: ");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i("onTouchEvent", "MyFrameLayout ACTION_UP: ");
                break;

        }
        boolean b = super.onTouchEvent(event);
        Log.i("onTouchEvent", "MyFrameLayout onTouchEvent: " + b);
        return b;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean b = super.onInterceptTouchEvent(ev);
        Log.i("onInterceptTouchEvent", "MyFrameLayout onInterceptTouchEvent: " + b);
        return false;
    }
}
