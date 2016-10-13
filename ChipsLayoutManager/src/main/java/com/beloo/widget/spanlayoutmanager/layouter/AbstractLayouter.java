package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.util.Pair;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;

abstract class AbstractLayouter implements ILayouter {
    protected int currentViewWidth;
    protected int currentViewHeight;
    protected int currentViewBottom;
    protected List<Pair<Rect, View>> rowViews = new LinkedList<>();
    protected int viewBottom;
    protected int viewTop;
    protected int rowSize = 0;
    protected int previousRowSize;

    protected ChipsLayoutManager layoutManager;

    AbstractLayouter(ChipsLayoutManager layoutManager, int topOffset, int bottomOffset) {
        this.layoutManager = layoutManager;
        this.viewTop = topOffset;
        this.viewBottom = bottomOffset;
    }

    int getCanvasWidth() {
        return layoutManager.getWidth();
    }

    int getCanvasHeight() {
        return layoutManager.getHeight();
    }

    public int getViewTop() {
        return viewTop;
    }

    public int getViewBottom() {
        return viewBottom;
    }

    @Override
    public void calculateView(View view) {
        currentViewHeight = layoutManager.getDecoratedMeasuredHeight(view);
        currentViewWidth = layoutManager.getDecoratedMeasuredWidth(view);

        currentViewBottom = layoutManager.getDecoratedBottom(view);
    }

    @Override
    public int getPreviousRowSize() {
        return previousRowSize;
    }

    @CallSuper
    @Override
    /** Read layouter state from current attached view. We need only last of it, but we can't determine here which is last.
     * Based on characteristics of last attached view, layouter algorithm will be able to continue placing from it.
     * This method have to be called on attaching view*/
    public void onAttachView(View view) {
        rowSize++;
    }

    @CallSuper
    @Override
    public void layoutRow() {
        previousRowSize = rowSize;
        this.rowSize = 0;
    }


}
