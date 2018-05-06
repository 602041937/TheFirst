package com.hpd.node;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * getSecondaryHorizontal()不知道为什么不起作用，数值跟getPrimaryHorizontal()一样
 */
@SuppressLint("AppCompatCustomView")
public class HPDSelectableTextView extends TextView {

    private static final int DEFAULT_SELECT_LENGTH = 1;
    private SelectionInfo mSelectionInfo = null;
    private int mTouchX;
    private int mTouchY;
    private TextView mTextView;
    private int[] mLocation = new int[2];
    private Spannable mSpannable;
    private BackgroundColorSpan mBackgroundColorSpan;
    private Paint paint;

    private MyScrollView scrollView;

    public ScrollView getScrollView() {
        return scrollView;
    }

    public void setScrollView(MyScrollView scrollView) {
        this.scrollView = scrollView;
    }

    private int circleRadius = 10;

    public HPDSelectableTextView(Context context) {
        this(context, null);
    }

    public HPDSelectableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HPDSelectableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    boolean isStartCursor;

    private void init() {

        mTextView = this;
        mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
        mSpannable = (Spannable) getText();
        mBackgroundColorSpan = new BackgroundColorSpan(Color.RED);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);

        setOnTouchListener(new OnTouchListener() {

            boolean isBegin;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Log.i("ssssss", "HPDSelectableTextView: ");
                mTouchX = (int) motionEvent.getX();
                mTouchY = (int) motionEvent.getY();
                Log.i("onTouch", "mTouchX: " + mTouchX);
                Log.i("onTouch", "mTouchY: " + mTouchY);

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //判断是否点击在游标的区域
                        if (mSelectionInfo != null) {

                            boolean isStart = Math.abs(mSelectionInfo.getStartX() - mTouchX) < 100
                                    && ((Math.abs(mSelectionInfo.getStartLineBound().top - mTouchY) < 100
                                    || (Math.abs(mSelectionInfo.getStartLineBound().bottom - mTouchY) < 100)));

                            boolean isEnd = Math.abs(mSelectionInfo.getEndX() - mTouchX) < 100
                                    && ((Math.abs(mSelectionInfo.getEndLineBound().top - mTouchY) < 100
                                    || (Math.abs(mSelectionInfo.getEndLineBound().bottom - mTouchY) < 100)));

                            if (isStart && isEnd) {
                                if (Math.abs(mSelectionInfo.getStartX() - mTouchX) > Math.abs(mSelectionInfo.getEndX() - mTouchX)) {
                                    isStartCursor = false;
                                    isBegin = true;
                                } else {
                                    isStartCursor = true;
                                    isBegin = true;
                                }
                                dealScrollView(false);
                                return true;
                            } else if (isStart) {
                                Log.i("abs", "abs: true");
                                Log.i("onTouch", "deal: ");
                                isStartCursor = true;
                                isBegin = true;
                                dealScrollView(false);
                                return true;
                            } else if (isEnd) {
                                isBegin = true;
                                isStartCursor = false;
                                dealScrollView(false);
                                return true;
                            }
                        }
                        dealScrollView(true);
                        break;

                    case MotionEvent.ACTION_MOVE:

                        //判断是否点击在游标的区域
                        if (mSelectionInfo != null && isBegin) {
                            dealMoveStarCursor(isStartCursor);
                            dealScrollView(false);
                            return true;
                        }

                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        isBegin = false;
                        break;
                }
//                dealScrollView(true);
                return false;
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                updateSelectionInfo();
//                float secondaryHorizontal = getLayout().getSecondaryHorizontal(charPosition);
//                Log.i("onLongClick", "secondaryHorizontal: " + secondaryHorizontal);
                getLocationInWindow(mLocation);
                Log.i("onLongClick", "mLocation[0]: " + mLocation[0]);
                Log.i("onLongClick", "mLocation[1]: " + mLocation[1]);
                isShow = true;
                return true;
            }
        });
    }

    private void dealScrollView(boolean intercept) {
//        if (scrollView != null) {
//            scrollView.setIntercept(intercept);
//        }
    }

    private void dealMoveStarCursor(boolean isLeft) {

        int position = TextLayoutUtil.getPreciseOffset(mTextView, mTouchX, mTouchY);
        //异常处理
        if (position > getText().toString().length()) {
            return;
        }

        if (isLeft) {
            mSelectionInfo.setStart(position);
            mSelectionInfo.setStartLine(getLayout().getLineForOffset(position));
            float starX = getLayout().getPrimaryHorizontal(mSelectionInfo.getStart());
            mSelectionInfo.setStartX(starX);

            Rect startLineBound = new Rect();
            getLayout().getLineBounds(mSelectionInfo.getStartLine(), startLineBound);
            mSelectionInfo.setStartLineBound(startLineBound);
        } else {
            mSelectionInfo.setEnd(position);
            mSelectionInfo.setEndLine(getLayout().getLineForOffset(position));
            float endX = getLayout().getPrimaryHorizontal(mSelectionInfo.getEnd());
            mSelectionInfo.setEndX(endX);

            Rect endLineBound = new Rect();
            getLayout().getLineBounds(mSelectionInfo.getEndLine(), endLineBound);
            mSelectionInfo.setEndLineBound(endLineBound);
        }

        if (mSelectionInfo.getStart() > mSelectionInfo.getEnd()) {
            mSelectionInfo.reverse();
            isStartCursor = !isStartCursor;
        }


        mSelectionInfo.setSelectionContent(
                mSpannable.subSequence(
                        mSelectionInfo.getStart(),
                        mSelectionInfo.getEnd()).toString());

        if (mBackgroundColorSpan != null) {
            mSpannable.removeSpan(mBackgroundColorSpan);
        }

        mSpannable.setSpan(mBackgroundColorSpan,
                mSelectionInfo.getStart(), mSelectionInfo.getEnd(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        invalidate();
    }

    private void updateSelectionInfo() {

        int startPosition = TextLayoutUtil.getPreciseOffset(mTextView, mTouchX, mTouchY);
        //异常处理
        if (startPosition >= getText().toString().length()) {
            return;
        }

        mSelectionInfo = new SelectionInfo();

        mSelectionInfo.setStart(startPosition);
        int endPosition = startPosition + DEFAULT_SELECT_LENGTH;
        mSelectionInfo.setEnd(endPosition);

        mSelectionInfo.setStartLine(getLayout().getLineForOffset(startPosition));
        mSelectionInfo.setEndLine(getLayout().getLineForOffset(endPosition));

        float starX = getLayout().getPrimaryHorizontal(mSelectionInfo.getStart());
        mSelectionInfo.setStartX(starX);

        float endX = getLayout().getPrimaryHorizontal(mSelectionInfo.getEnd());
        mSelectionInfo.setEndX(endX);

        Rect startLineBound = new Rect();
        getLayout().getLineBounds(mSelectionInfo.getStartLine(), startLineBound);
        mSelectionInfo.setStartLineBound(startLineBound);

        Rect endLineBound = new Rect();
        getLayout().getLineBounds(mSelectionInfo.getEndLine(), endLineBound);
        mSelectionInfo.setEndLineBound(endLineBound);

        mSelectionInfo.setSelectionContent(
                mSpannable.subSequence(
                        mSelectionInfo.getStart(),
                        mSelectionInfo.getEnd()).toString());

        if (mBackgroundColorSpan != null) {
            mSpannable.removeSpan(mBackgroundColorSpan);
        }

        mSpannable.setSpan(mBackgroundColorSpan,
                mSelectionInfo.getStart(), mSelectionInfo.getEnd(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    }


    private int lineWidth = 3;
    private boolean isShow = false;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (mSelectionInfo != null) {

            int start = mSelectionInfo.getStart();
            float startX = mSelectionInfo.getStartX();
            int startLine = mSelectionInfo.getStartLine();
            Rect startLineBound = mSelectionInfo.getStartLineBound();
            canvas.drawRect(startX - lineWidth, startLineBound.top, startX, startLineBound.bottom, paint);
            canvas.drawCircle(startX - lineWidth / 2, startLineBound.top - circleRadius, circleRadius, paint);

            int end = mSelectionInfo.getEnd();
            float endX = mSelectionInfo.getEndX();
            int endLine = mSelectionInfo.getEndLine();
            Rect endLineBound = mSelectionInfo.getEndLineBound();

            canvas.drawRect(endX - lineWidth, endLineBound.top, endX, endLineBound.bottom, paint);
            canvas.drawCircle(endX - lineWidth / 2, endLineBound.bottom + circleRadius, circleRadius, paint);
        }
    }
}
