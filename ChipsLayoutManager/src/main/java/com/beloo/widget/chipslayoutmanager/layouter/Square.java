package com.beloo.widget.chipslayoutmanager.layouter;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;

public class Square implements ICanvas {

    private ChipsLayoutManager chipsLayoutManager;

    public Square(ChipsLayoutManager chipsLayoutManager) {
        this.chipsLayoutManager = chipsLayoutManager;
    }

    public final int getCanvasRightBorder() {
        return chipsLayoutManager.getWidth() - chipsLayoutManager.getPaddingRight();
    }

    public final int getCanvasBottomBorder() {
        return chipsLayoutManager.getHeight();
    }

    public final int getCanvasLeftBorder() {
        return chipsLayoutManager.getPaddingLeft();
    }

    //todo why zero here but not top padding?
    public final int getCanvasTopBorder() {
        return 0;
    }

}
