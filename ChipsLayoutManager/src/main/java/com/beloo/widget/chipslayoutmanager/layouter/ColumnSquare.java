package com.beloo.widget.chipslayoutmanager.layouter;

import android.support.v7.widget.RecyclerView;

class ColumnSquare extends Square {

    ColumnSquare(RecyclerView.LayoutManager lm) {
        super(lm);
    }

    public final int getCanvasRightBorder() {
        return lm.getWidth();
    }

    public final int getCanvasBottomBorder() {
        return lm.getHeight() - lm.getPaddingBottom();
    }

    public final int getCanvasLeftBorder() {
        return 0;
    }

    public final int getCanvasTopBorder() {
        return lm.getPaddingTop();
    }

    @Override
    public int getEnd() {
        return getCanvasRightBorder();
    }

    @Override
    public int getEndAfterPadding() {
        return lm.getWidth() - lm.getPaddingRight();
    }

    @Override
    public int getStart() {
        return 0;
    }

    @Override
    public int getStartAfterPadding() {
        return lm.getPaddingLeft();
    }
}
