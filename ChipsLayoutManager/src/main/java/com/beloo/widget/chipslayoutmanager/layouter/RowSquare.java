package com.beloo.widget.chipslayoutmanager.layouter;

import android.support.v7.widget.RecyclerView;

class RowSquare extends Square {

    RowSquare(RecyclerView.LayoutManager lm) {
        super(lm);
    }

    public final int getCanvasRightBorder() {
        return lm.getWidth() - lm.getPaddingRight();
    }

    /** get bottom border. Controlled by clipToPadding property*/
    public final int getCanvasBottomBorder() {
        return lm.getHeight();
    }

    public final int getCanvasLeftBorder() {
        return lm.getPaddingLeft();
    }

    /** get bottom border. Controlled by clipToPadding property*/
    public final int getCanvasTopBorder() {
        return 0;
    }

}
