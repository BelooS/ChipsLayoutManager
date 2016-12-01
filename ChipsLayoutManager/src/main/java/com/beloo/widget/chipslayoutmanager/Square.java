package com.beloo.widget.chipslayoutmanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class Square implements ICanvas {

    private RecyclerView.LayoutManager lm;

    Square(RecyclerView.LayoutManager lm) {
        this.lm = lm;
    }

    public final int getCanvasRightBorder() {
        return lm.getWidth() - lm.getPaddingRight();
    }

    public final int getCanvasBottomBorder() {
        return lm.getHeight() - lm.getPaddingBottom();
    }

    public final int getCanvasLeftBorder() {
        return lm.getPaddingLeft();
    }

    public final int getCanvasTopBorder() {
        return lm.getPaddingTop();
    }

    @Override
    public Rect getCanvasRect() {
        return new Rect(getCanvasLeftBorder(), getCanvasTopBorder(), getCanvasRightBorder(), getCanvasBottomBorder());
    }

    @Override
    public Rect getViewRect(View view) {
        int left = lm.getDecoratedLeft(view);
        int top = lm.getDecoratedTop(view);
        int right = lm.getDecoratedRight(view);
        int bottom = lm.getDecoratedBottom(view);
        return new Rect(left, top, right, bottom);
    }

    @Override
    public boolean isInside(Rect rectCandidate) {
        //intersection changes rect!!!
        Rect intersect = new Rect(rectCandidate);
        return getCanvasRect().intersect(intersect);
    }

    @Override
    public boolean isInside(View viewCandidate) {
        return isInside(getViewRect(viewCandidate));
    }

    @Override
    public boolean isFullyVisible(View view) {
        Rect rect = getViewRect(view);
        return isFullyVisible(rect);
    }

    @Override
    public boolean isFullyVisible(Rect rect) {
        return rect.top >= getCanvasTopBorder()
                && rect.bottom <= getCanvasBottomBorder()
                && rect.left >= getCanvasLeftBorder()
                && rect.right <= getCanvasRightBorder();
    }

}
