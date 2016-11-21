package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;

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

}
