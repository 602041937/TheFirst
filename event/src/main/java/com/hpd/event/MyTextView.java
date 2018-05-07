package com.hpd.event;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class MyTextView extends AppCompatTextView {


    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean b = super.dispatchTouchEvent(ev);
        Log.i("dispatchTouchEvent", "MyTextView: dispatchTouchEvent " + b);
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("onTouchEvent", "MyTextView ACTION_DOWN: ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("onTouchEvent", "MyTextView ACTION_MOVE: ");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i("onTouchEvent", "MyTextView ACTION_UP: ");
                break;

        }
        boolean b = super.onTouchEvent(event);
        Log.i("dispatchTouchEvent", "MyTextView: onTouchEvent " + b);
        return b;
    }
}
