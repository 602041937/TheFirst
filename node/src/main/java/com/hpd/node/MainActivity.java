package com.hpd.node;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    MyScrollView scrollView;
    private HPDSelectableTextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.scrollView);
        textView = findViewById(R.id.textView);
        textView.setScrollView(scrollView);


//        scrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Log.i("ssssss", "scrollView: ");
////                switch (motionEvent.getAction()) {
////                    case MotionEvent.ACTION_DOWN:
////                        return true;
////                    case MotionEvent.ACTION_MOVE:
////                        return false;
////                    case MotionEvent.ACTION_UP:
////                        return false;
////
////                }
//                return false;
//            }
//        });
//
//        scrollView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        scrollView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                return false;
//            }
//        });


    }
}
