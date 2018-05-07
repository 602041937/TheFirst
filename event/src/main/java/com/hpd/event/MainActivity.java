package com.hpd.event;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean b = super.dispatchTouchEvent(ev);
        Log.i("dispatchTouchEvent", "MainActivity: dispatchTouchEvent " + b);
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("onTouchEvent", "MainActivity ACTION_DOWN: ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("onTouchEvent", "MainActivity ACTION_MOVE: ");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i("onTouchEvent", "MainActivity ACTION_UP: ");
                break;

        }
        boolean b = super.onTouchEvent(event);
        Log.i("onTouchEvent", "MainActivity onTouchEvent: " + b);
        return b;
    }

}
