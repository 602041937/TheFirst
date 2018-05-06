package com.hpd.thefirst;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView textView;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                Layout layout = textView.getLayout();
                int lineForVertical = layout.getLineForVertical((int) y);
                int offsetForHorizontal = layout.getOffsetForHorizontal(lineForVertical, x);

                Log.i(TAG, "onTouch: lineForVertical" + lineForVertical);
                Log.i(TAG, "onTouch: offsetForHorizontal" + offsetForHorizontal);
                return false;
            }
        });

    }


}
