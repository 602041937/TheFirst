package com.hpd.node;

import android.graphics.Color;
import android.graphics.Rect;
import android.text.style.BackgroundColorSpan;

/**
 * Created by Jaeger on 16/8/30.
 * <p>
 * Email: chjie.jaeger@gmail.com
 * GitHub: https://github.com/laobie
 */
public class SelectionInfo {

    private int start;
    private int startLine;
    private float startX;
    private Rect startLineBound;
    private int end;
    private int endLine;
    private float endX;
    private Rect endLineBound;
    private String selectionContent;
    private String comment;
    private BackgroundColorSpan commentSpan = new BackgroundColorSpan(Color.YELLOW);

    public BackgroundColorSpan getCommentSpan() {
        return commentSpan;
    }

    public void setCommentSpan(BackgroundColorSpan commentSpan) {
        this.commentSpan = commentSpan;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void reverse() {

        int tempPosition = start;
        int tempLine = startLine;
        float tempX = startX;
        Rect tempLineBound = startLineBound;

        start = end;
        startLine = endLine;
        startX = endX;
        startLineBound = endLineBound;

        end = tempPosition;
        endLine = tempLine;
        endX = tempX;
        endLineBound = tempLineBound;
    }

    public Rect getStartLineBound() {
        return startLineBound;
    }

    public void setStartLineBound(Rect startLineBound) {
        this.startLineBound = startLineBound;
    }

    public Rect getEndLineBound() {
        return endLineBound;
    }

    public void setEndLineBound(Rect endLineBound) {
        this.endLineBound = endLineBound;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public String getSelectionContent() {
        return selectionContent;
    }

    public void setSelectionContent(String selectionContent) {
        this.selectionContent = selectionContent;
    }
}
