package com.hpd.node;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ClickableUnderLineSpan extends ClickableSpan {

    @Override
    public void onClick(View widget) {
        Log.i("ClickableUnderLineSpan", "onClick: ");
        Toast.makeText(widget.getContext(), "我呗点击了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateDrawState(TextPaint ds) {

        ds.setColor(Color.GREEN);
        ds.setUnderlineText(true);
    }
}
