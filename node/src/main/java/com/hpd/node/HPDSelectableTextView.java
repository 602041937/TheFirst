package com.hpd.node;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;


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
    private ArrayList<SelectionInfo> lines = new ArrayList<>();

    private OperateWindow mOperateWindow;
    private ScrollView mScrollView;

    public ScrollView getScrollView() {
        return mScrollView;
    }

    public void setScrollView(ScrollView mScrollView) {

        this.mScrollView = mScrollView;

        this.mScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("mScrollView", "ACTION_DOWN: ");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("mScrollView", "ACTION_MOVE: ");
                        if (mOperateWindow != null && mOperateWindow.isShowing()) {
                            mOperateWindow.dismiss();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        //加上postDelayed，因为猛滚动，然后放下，scrollview会再滑动一段距离，导致pop出现偏差
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mOperateWindow != null) {
                                    mOperateWindow.show();
                                }
                            }
                        }, 300);
                        break;
                }
                return false;
            }
        });
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
    boolean isBegin;


    private void init() {

        mTextView = this;
        mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
        mSpannable = (Spannable) getText();
        mBackgroundColorSpan = new BackgroundColorSpan(Color.RED);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);

        mOperateWindow = new OperateWindow();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Log.i("ssssss", "HPDSelectableTextView: ");
                mTouchX = (int) motionEvent.getX();
                mTouchY = (int) motionEvent.getY();
                Log.i("onTouch", "mTouchX: " + mTouchX);
                Log.i("onTouch", "mTouchY: " + mTouchY);

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("onTouch", "HPDSelectableTextView: ACTION_DOWN");
                        //判断是否点击在游标的区域
                        getParent().requestDisallowInterceptTouchEvent(false);
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
                                return true;
                            } else if (isStart) {
                                isStartCursor = true;
                                isBegin = true;
                                return true;
                            } else if (isEnd) {
                                isBegin = true;
                                isStartCursor = false;
                                return true;
                            }
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:

                        Log.i("onTouch", "HPDSelectableTextView: ACTION_MOVE");
                        getParent().requestDisallowInterceptTouchEvent(false);
                        //判断是否点击在游标的区域
                        if (mSelectionInfo != null && isBegin) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                            dealMoveStarCursor(isStartCursor);
                        }
                        if (mOperateWindow != null && mOperateWindow.isShowing()) {
                            mOperateWindow.dismiss();
                        }
                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:

                        getParent().requestDisallowInterceptTouchEvent(true);
                        Log.i("onTouch", "HPDSelectableTextView: ACTION_UP");
                        isBegin = false;
                        if (mOperateWindow != null) {
                            mOperateWindow.show();
                        }
                        break;
                }
                return false;
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                updateSelectionInfo();
                getLocationInWindow(mLocation);
                Log.i("onLongClick", "mLocation[0]: " + mLocation[0]);
                Log.i("onLongClick", "mLocation[1]: " + mLocation[1]);
                //true:不再触发onclick事件
                return true;
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //当用户点击text，取消选中
                if (mSelectionInfo != null) {
                    mSelectionInfo = null;
                    clearSelectState();
                    mOperateWindow.dismiss();
                } else {
                    for (SelectionInfo info : lines) {
                        int preciseOffset = TextLayoutUtil.getPreciseOffset(mTextView, mTouchX, mTouchY);
                        if (preciseOffset >= info.getStart() && preciseOffset <= info.getEnd()) {
                            Toast.makeText(getContext(), "删除划线", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void clearSelectState() {

        mSelectionInfo = null;
        if (mBackgroundColorSpan != null) {
            mSpannable.removeSpan(mBackgroundColorSpan);
        }
        invalidate();
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

        mOperateWindow.show();
    }


    private int lineWidth = 3;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (mSelectionInfo != null) {
            paint.setColor(Color.GREEN);
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


        paint.setColor(Color.BLUE);
        paint.setStrokeWidth((float) 1.0);
        //划线
        for (SelectionInfo line : lines) {
            //只在一行的情况下
            if (line.getStartLine() == line.getEndLine()) {
                canvas.drawLine(line.getStartX(), line.getStartLineBound().bottom, line.getEndX(), line.getEndLineBound().bottom, paint);
            } else if (line.getStartLine() != line.getEndLine()) {
                //不在同一行的情况下
                canvas.drawLine(line.getStartX(), line.getStartLineBound().bottom, line.getStartLineBound().right, line.getStartLineBound().bottom, paint);
                for (int i = line.getStartLine() + 1; i <= line.getEndLine(); i++) {
                    if (i == line.getEndLine()) {
                        //最后一行的情况
                        canvas.drawLine(line.getEndLineBound().left, line.getEndLineBound().bottom, line.getEndX(), line.getEndLineBound().bottom, paint);
                    } else {
                        //中间行
                        int lineBottom = getLayout().getLineBottom(i);
                        canvas.drawLine(line.getEndLineBound().left, lineBottom, line.getEndLineBound().right, lineBottom, paint);
                    }
                }
            }

            for (int i = line.getStartLine(); i <= line.getEndLine(); i++) {

            }
        }
    }


    /*
     * Operate windows : copy, select all
     */
    private class OperateWindow {

        private PopupWindow mWindow;
        private TextView tvDrawLine;
        private View point;
        private int mWidth;
        private int mHeight;

        private OperateWindow() {
            // 解析弹出的菜单
            final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.option_view, null);
            tvDrawLine = contentView.findViewById(R.id.draw_line);
            tvDrawLine.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectionInfo == null) {
                        return;
                    }
                    if (TextUtils.isEmpty(mSelectionInfo.getSelectionContent())) {
                        return;
                    }
//                    Log.i("onClick", "onClick: " + mSelectionInfo.getSelectionContent());
//                    DrawLine drawLine = new DrawLine();
//                    drawLine.setStart(mSelectionInfo.getStart());
//                    drawLine.setEnd(mSelectionInfo.getEnd());
                    lines.add(mSelectionInfo);
                    mSelectionInfo = null;
                    clearSelectState();
                    dismiss();
                    invalidate();
//                    updateLineSpan();
                }
            });
            contentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mWidth = contentView.getMeasuredWidth();
            mHeight = contentView.getMeasuredHeight();
            // 通过PopWindow弹出
            mWindow = new PopupWindow(contentView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, false);
            mWindow.setClippingEnabled(false);
            point = contentView.findViewById(R.id.point);
        }

        /**
         * 显示弹窗的方法
         */
        private void show() {

            if (mSelectionInfo == null) {
                return;
            }
            if (TextUtils.isEmpty(mSelectionInfo.getSelectionContent())) {
                return;
            }

            // 获取在当前窗口内的绝对坐标
            mTextView.getLocationInWindow(mLocation);
            // 定位弹窗位置
            Layout layout = mTextView.getLayout();
            // 得到当前字符段的左边X坐标+Y坐标
            float posX = layout.getPrimaryHorizontal(mSelectionInfo.getStart()) + mLocation[0] + mTextView.getPaddingLeft();
            float posRightX = layout.getPrimaryHorizontal(mSelectionInfo.getEnd()) + mLocation[0] + mTextView.getPaddingLeft();
            posX = posX + (posRightX - posX) / 2 - mWidth / 2;
            int posY = layout.getLineTop(layout.getLineForOffset(
                    mSelectionInfo.getStart())) + mLocation[1] + mTextView.getPaddingTop() - mHeight - 5;

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) point.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            point.setLayoutParams(layoutParams);
            if (posX < 0) {
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) point.getLayoutParams();
                layoutParams2.setMargins((int) posX, 0, 0, 0);
                point.setLayoutParams(layoutParams2);
                posX = 0;
            }

            if ((posX + mWidth) > TextLayoutUtil.getScreenWidth(getContext())) {
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) point.getLayoutParams();
                layoutParams2.setMargins((int) (posX + mWidth) - TextLayoutUtil.getScreenWidth(getContext()), 0, 0, 0);
                point.setLayoutParams(layoutParams2);
                posX = TextLayoutUtil.getScreenWidth(getContext()) - mWidth;
            }

            mWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, (int) posX, posY);
        }

        private void dismiss() {
            mWindow.dismiss();
        }

        public boolean isShowing() {
            return mWindow.isShowing();
        }

        /**
         * 设置弹窗菜单是否能够使用删除按钮
         *
         * @param del:是否显示删除按钮的Boolean值变量
         */
        private void setDel(boolean del) {
            tvDrawLine.setEnabled(del);
        }
    }


}
