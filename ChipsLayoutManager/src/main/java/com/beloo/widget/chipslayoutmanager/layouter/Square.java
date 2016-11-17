package com.beloo.widget.chipslayoutmanager.layouter;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;

class Square implements ICanvas {

    private ChipsLayoutManager chipsLayoutManager;

    Square(ChipsLayoutManager chipsLayoutManager) {
        this.chipsLayoutManager = chipsLayoutManager;
    }

    public final int getCanvasRightBorder() {
        return chipsLayoutManager.getWidth() - chipsLayoutManager.getPaddingRight();
    }

    public final int getCanvasBottomBorder() {
        return chipsLayoutManager.getHeight() - chipsLayoutManager.getPaddingBottom();
    }

    public final int getCanvasLeftBorder() {
        return chipsLayoutManager.getPaddingLeft();
    }

    public final int getCanvasTopBorder() {
        return chipsLayoutManager.getPaddingTop();
    }

}
