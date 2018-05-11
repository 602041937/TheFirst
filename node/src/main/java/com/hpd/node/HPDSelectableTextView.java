package com.hpd.node;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * getSecondaryHorizontal()不知道为什么不起作用，数值跟getPrimaryHorizontal()一样
 */
@SuppressLint("AppCompatCustomView")
public class HPDSelectableTextView extends TextView {

    //默认选择文本的宽度
    private static final int DEFAULT_SELECT_LENGTH = 1;
    //游标的竖线的宽度
    private static final int CURSOR_LINE_WIDTH = 3;
    //游标的圆形直径
    private static final int CURSOR_CIRCLE_DIAMETER = 10;

    public int OPERATE_WINDOW_DRAW_LINE = 0, OPERATE_WINDOW_COMMENT = 1, OPERATE_WINDOW_WORD = 2;
    private SelectionInfo mSelectionInfo = null;
    private int mTouchX;
    private int mTouchY;
    private TextView mTextView;
    private int[] mLocation = new int[2];
    private Spannable mSpannable;
    private BackgroundColorSpan mBackgroundColorSpan;
    private Paint paint;
    private ArrayList<SelectionInfo> lines = new ArrayList<>();

    private ArrayList<SelectionInfo> comments = new ArrayList<>();

    private OperateWindow mOperateWindow;
    private ObservableScrollView mScrollView;

    public ScrollView getScrollView() {
        return mScrollView;
    }

    public void setScrollView(ObservableScrollView mScrollView) {

        this.mScrollView = mScrollView;
        this.mScrollView.setOnScrollListener(new ObservableScrollView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(ObservableScrollView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        if (mOperateWindow != null) {
                            mOperateWindow.show(OPERATE_WINDOW_DRAW_LINE, OPERATE_WINDOW_COMMENT);
                        }
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        if (mOperateWindow != null && mOperateWindow.isShowing()) {
                            mOperateWindow.dismiss();
                        }
                        break;
                    case SCROLL_STATE_FLING:
                        break;
                }
            }

            @Override
            public void onScroll(ObservableScrollView view, boolean isTouchScroll, int l, int t, int oldl, int oldt) {

            }
        });
    }


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
    private int COMMENT_CLICK_ADD_OFFSET = 10;

    private void init() {

        mTextView = this;
        mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
        mSpannable = (Spannable) getText();
        mBackgroundColorSpan = new BackgroundColorSpan(Color.RED);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);

        mOperateWindow = new OperateWindow();

        setOnTouchListener(new OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                mTouchX = (int) motionEvent.getX() - getPaddingLeft();
                mTouchY = (int) motionEvent.getY() - getPaddingTop();
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
                            mOperateWindow.show(OPERATE_WINDOW_DRAW_LINE, OPERATE_WINDOW_COMMENT);
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

                    for (SelectionInfo info : comments) {
                        if (mTouchX >= info.getEndX() - COMMENT_CLICK_ADD_OFFSET && mTouchX <= info.getEndX() + getCommentBitmap().getWidth() + COMMENT_CLICK_ADD_OFFSET) {
                            if (mTouchY >= info.getEndLineBound().bottom - COMMENT_CLICK_ADD_OFFSET && mTouchY <= info.getEndLineBound().bottom + getCommentBitmap().getHeight() + COMMENT_CLICK_ADD_OFFSET) {
                                Toast.makeText(getContext(), "点击comment," + info.getComment(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    for (SelectionInfo info : lines) {
                        int preciseOffset = TextLayoutUtil.getPreciseOffset(mTextView, mTouchX, mTouchY);
                        if (preciseOffset >= info.getStart() && preciseOffset <= info.getEnd()) {
                            lines.remove(info);
                            Toast.makeText(getContext(), "删除划线", Toast.LENGTH_SHORT).show();
                            invalidate();
                            break;
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

        mOperateWindow.show(OPERATE_WINDOW_DRAW_LINE, OPERATE_WINDOW_COMMENT);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (mSelectionInfo != null) {

            paint.setColor(Color.GREEN);
            float startX = mSelectionInfo.getStartX() + getPaddingLeft();
            Rect startLineBound = mSelectionInfo.getStartLineBound();
            canvas.drawRect(startX - CURSOR_LINE_WIDTH, startLineBound.top + getPaddingTop(), startX, startLineBound.bottom + getPaddingTop(), paint);
            canvas.drawCircle(startX - CURSOR_LINE_WIDTH / 2, startLineBound.top - CURSOR_CIRCLE_DIAMETER + getPaddingTop(), CURSOR_CIRCLE_DIAMETER, paint);

            float endX = mSelectionInfo.getEndX() + getPaddingLeft();
            Rect endLineBound = mSelectionInfo.getEndLineBound();

            canvas.drawRect(endX - CURSOR_LINE_WIDTH, endLineBound.top + getPaddingTop(), endX, endLineBound.bottom + getPaddingTop(), paint);
            canvas.drawCircle(endX - CURSOR_LINE_WIDTH / 2, endLineBound.bottom + CURSOR_CIRCLE_DIAMETER + getPaddingTop(), CURSOR_CIRCLE_DIAMETER, paint);
        }


        paint.setColor(Color.BLUE);
        paint.setStrokeWidth((float) 1.0);
        //划线
        for (SelectionInfo line : lines) {
            //只在一行的情况下
            if (line.getStartLine() == line.getEndLine()) {
                canvas.drawLine(line.getStartX() + getPaddingLeft(), line.getStartLineBound().bottom + getPaddingTop(), line.getEndX() + getPaddingLeft(), line.getEndLineBound().bottom + getPaddingTop(), paint);
            } else if (line.getStartLine() != line.getEndLine()) {
                //不在同一行的情况下
                canvas.drawLine(line.getStartX() + getPaddingLeft(), line.getStartLineBound().bottom + getPaddingTop(), line.getStartLineBound().right + getPaddingLeft(), line.getStartLineBound().bottom + getPaddingTop(), paint);
                for (int i = line.getStartLine() + 1; i <= line.getEndLine(); i++) {
                    if (i == line.getEndLine()) {
                        //最后一行的情况
                        canvas.drawLine(line.getEndLineBound().left + getPaddingLeft(), line.getEndLineBound().bottom + getPaddingTop(), line.getEndX() + getPaddingLeft(), line.getEndLineBound().bottom + getPaddingTop(), paint);
                    } else {
                        //中间行
                        int lineBottom = getLayout().getLineBottom(i) + getPaddingTop();
                        canvas.drawLine(line.getEndLineBound().left + getPaddingLeft(), lineBottom, line.getEndLineBound().right + getPaddingLeft(), lineBottom, paint);
                    }
                }
            }
        }

        //画标注
        for (SelectionInfo comment : comments) {
            mSpannable.setSpan(comment.getCommentSpan(), comment.getStart(), comment.getEnd(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            canvas.drawBitmap(getCommentBitmap(), comment.getEndX() + getPaddingLeft(), comment.getEndLineBound().bottom - 10 + getPaddingTop(), null);
        }
    }

    private Bitmap dstbmp;

    private Bitmap getCommentBitmap() {
        if (dstbmp == null) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_comment);
            Matrix matrix = new Matrix();
            matrix.postScale(0.35f, 0.35f);
            dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }
        return dstbmp;
    }

    /*
     * Operate windows : copy, select all
     */
    private class OperateWindow {

        private PopupWindow mWindow;
        private TextView tvDrawLine, tvComment, tvWord;
        private View point;
        private int mWidth;
        private int mHeight;

        private OperateWindow() {
            // 解析弹出的菜单
            @SuppressLint("InflateParams") final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.option_view, null, false);

            //划线
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

                    boolean isCanTogether = true;
                    lines.add(mSelectionInfo);

                    B:
                    while (isCanTogether) {
                        for (int i = 0; i < lines.size() - 1; i++) {
                            SelectionInfo infoI = lines.get(i);
                            for (int j = i + 1; j < lines.size(); j++) {
                                SelectionInfo infoJ = lines.get(j);
                                if (infoI.getStart() < infoJ.getStart() && infoI.getEnd() >= infoJ.getStart()) {
                                    infoJ.setStart(infoI.getStart());
                                    infoJ.setStartX(infoI.getStartX());
                                    infoJ.setStartLine(infoI.getStartLine());
                                    infoJ.setStartLineBound(infoI.getStartLineBound());
                                    if (infoI.getEnd() > infoJ.getEnd()) {
                                        infoJ.setEnd(infoI.getEnd());
                                        infoJ.setEndLine(infoI.getEndLine());
                                        infoJ.setEndLineBound(infoI.getEndLineBound());
                                        infoJ.setEndX(infoI.getEndX());
                                    }
                                    lines.remove(infoI);
                                    continue B;
                                } else if (infoI.getStart() >= infoJ.getStart() && infoI.getStart() <= infoJ.getEnd()) {
                                    isCanTogether = true;
                                    if (infoI.getEnd() > infoJ.getEnd()) {
                                        infoJ.setEnd(infoI.getEnd());
                                        infoJ.setEndLine(infoI.getEndLine());
                                        infoJ.setEndLineBound(infoI.getEndLineBound());
                                        infoJ.setEndX(infoI.getEndX());
                                    }
                                    lines.remove(infoI);
                                    continue B;
                                }
                            }
                        }
                        isCanTogether = false;
                    }
                    mSelectionInfo = null;
                    clearSelectState();
                    dismiss();
                    invalidate();
                }
            });

            //批注
            tvComment = contentView.findViewById(R.id.comment);
            tvComment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mSelectionInfo == null) {
                        return;
                    }
                    if (TextUtils.isEmpty(mSelectionInfo.getSelectionContent())) {
                        return;
                    }

                    final ArrayList<SelectionInfo> objects = new ArrayList<>();
                    final StringBuilder stringBuilder = new StringBuilder();
                    for (SelectionInfo info : comments) {
                        if (!(mSelectionInfo.getEnd() < info.getStart() || mSelectionInfo.getStart() > info.getEnd())) {
                            objects.add(info);
                            stringBuilder.append(info.getComment()).append(" sss ");
                        }
                    }

                    //暂时使用
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("合并");
                    builder.setMessage("" + stringBuilder.toString());
                    //点击对话框以外的区域是否让对话框消失
                    builder.setCancelable(false);
                    //设置正面按钮
                    builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //处理合并comment
                            //逻辑
                            objects.add(mSelectionInfo);
                            SelectionInfo mixStartInfo = mSelectionInfo;
                            SelectionInfo maxEndInfo = mSelectionInfo;
                            for (SelectionInfo info : objects) {
                                if (info.getStart() < mixStartInfo.getStart()) {
                                    mixStartInfo = info;
                                }
                                if (info.getEnd() > maxEndInfo.getEnd()) {
                                    maxEndInfo = info;
                                }
                            }

                            mSelectionInfo.setStart(mixStartInfo.getStart());
                            mSelectionInfo.setStartX(mixStartInfo.getStartX());
                            mSelectionInfo.setStartLine(mixStartInfo.getStartLine());
                            mSelectionInfo.setStartLineBound(mixStartInfo.getStartLineBound());
                            mSelectionInfo.setEnd(maxEndInfo.getEnd());
                            mSelectionInfo.setEndX(maxEndInfo.getEndX());
                            mSelectionInfo.setEndLine(maxEndInfo.getEndLine());
                            mSelectionInfo.setEndLineBound(maxEndInfo.getEndLineBound());

                            mSelectionInfo.setComment(stringBuilder.toString() + mSelectionInfo.getStart());

                            comments.removeAll(objects);

                            comments.add(mSelectionInfo);
                            Collections.sort(comments, new Comparator<SelectionInfo>() {
                                @Override
                                public int compare(SelectionInfo o1, SelectionInfo o2) {
                                    return o1.getStart() - o2.getStart();
                                }
                            });
                            clearSelectState();
                            dialog.dismiss();
                            dismiss();
                            invalidate();
                        }
                    });

                    //设置反面按钮
                    builder.setNegativeButton("不是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    //显示对话框
                    dialog.show();
                }
            });

            tvWord = contentView.findViewById(R.id.word);

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
        private void show(int... operates) {

            if (mSelectionInfo == null) {
                return;
            }
            if (TextUtils.isEmpty(mSelectionInfo.getSelectionContent())) {
                return;
            }

            tvDrawLine.setVisibility(GONE);
            tvComment.setVisibility(GONE);
            tvWord.setVisibility(GONE);

            for (int item : operates) {
                if (item == OPERATE_WINDOW_DRAW_LINE) {
                    tvDrawLine.setVisibility(VISIBLE);
                } else if (item == OPERATE_WINDOW_COMMENT) {
                    tvComment.setVisibility(VISIBLE);
                } else if (item == OPERATE_WINDOW_WORD) {
                    tvWord.setVisibility(VISIBLE);
                }
            }

            View contentView = mWindow.getContentView();
            contentView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

            mWidth = contentView.getMeasuredWidth();
            mHeight = contentView.getMeasuredHeight();

            // 获取在当前窗口内的绝对坐标
            mTextView.getLocationInWindow(mLocation);
            int[] scrollViewLocation = new int[2];
            mTextView.getLocationOnScreen(scrollViewLocation);

            float resultX;
            float resultY;

            // 得到当前字符段的左边X坐标+Y坐标
            float startX = mSelectionInfo.getStartX() + mLocation[0] + getPaddingLeft();
            float endX = mSelectionInfo.getEndX() + mLocation[0] + getPaddingLeft();

            resultX = startX + (endX - startX) / 2 - mWidth / 2;

            //重置箭头的位置
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) point.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            point.setLayoutParams(layoutParams);

            //如果pop显示小于临界值，调整到临界值，并且把箭头对准所选区域的中间
            if (resultX < 0) {
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) point.getLayoutParams();
                layoutParams2.setMargins((int) resultX, 0, 0, 0);
                point.setLayoutParams(layoutParams2);
                resultX = 0;
            }

            //如果pop显示大于临界值，调整到临界值，并且把箭头对准所选区域的中间
            if ((resultX + mWidth) > TextLayoutUtil.getScreenWidth(getContext())) {
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) point.getLayoutParams();
                layoutParams2.setMargins((int) (resultX + mWidth) - TextLayoutUtil.getScreenWidth(getContext()), 0, 0, 0);
                point.setLayoutParams(layoutParams2);
                resultX = TextLayoutUtil.getScreenWidth(getContext()) - mWidth;
            }

            //取得开始位置的y
            int startY = mSelectionInfo.getStartLineBound().top + getPaddingTop() + mLocation[1] - mHeight;
            mScrollView.getLocationInWindow(scrollViewLocation);
            if (startY <= scrollViewLocation[1] + mScrollView.getPaddingTop()) {
                //需要显示在所选区域下边
                int endY = mSelectionInfo.getEndLineBound().bottom + getPaddingTop() + mLocation[1];
                //如果下边的显示区域超过临界值，那就不要显示了。
                if (endY < scrollViewLocation[1] + mScrollView.getPaddingTop()) {
                    return;
                } else if (endY > scrollViewLocation[1] - mScrollView.getPaddingTop() + mScrollView.getHeight() - mHeight) {
                    //最后的就是选择区域充满全屏幕，就显示在中间
                    resultX = scrollViewLocation[0] + mScrollView.getWidth() / 2 - mWidth / 2;
                    resultY = scrollViewLocation[1] + mScrollView.getHeight() / 2 - mHeight / 2;
                } else {
                    resultY = endY;
                }
            } else {
                if (startY > scrollViewLocation[1] - mScrollView.getPaddingTop() + mScrollView.getHeight() - mHeight) {
                    return;
                }
                resultY = startY;
            }
            mWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, (int) resultX, (int) resultY);
        }

        private void dismiss() {
            mWindow.dismiss();
        }

        public boolean isShowing() {
            return mWindow.isShowing();
        }
    }
}
