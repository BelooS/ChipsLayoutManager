package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import com.beloo.widget.spanlayoutmanager.ChipsLayoutManager;
import com.beloo.widget.spanlayoutmanager.SpanLayoutChildGravity;
import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.spanlayoutmanager.gravity.GravityModifiersFactory;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.spanlayoutmanager.gravity.IGravityModifier;

abstract class AbstractLayouter implements ILayouter {
    int currentViewWidth;
    int currentViewHeight;
    private int currentViewPosition;
    List<Pair<Rect, View>> rowViews = new LinkedList<>();
    /** bottom of current row*/
    int rowBottom;
    /** top of current row*/
    int rowTop;

    @Nullable
    private Integer leftBorderOfPreviouslyAttachedView = null;

    /** Max items in row restriction. Layout of row should be stopped when this count of views reached*/
    @Nullable
    private Integer maxViewsInRow = null;

    private int rowSize = 0;
    private int previousRowSize;

    private ChipsLayoutManager layoutManager;
    private IViewCacheStorage cacheStorage;

    private IChildGravityResolver childGravityResolver;
    private GravityModifiersFactory gravityModifiersFactory = new GravityModifiersFactory();

    AbstractLayouter(ChipsLayoutManager layoutManager, int topOffset, int bottomOffset, IViewCacheStorage cacheStorage, IChildGravityResolver childGravityResolver) {
        this.layoutManager = layoutManager;
        this.rowTop = topOffset;
        this.rowBottom = bottomOffset;
        this.cacheStorage = cacheStorage;
        this.childGravityResolver = childGravityResolver;
    }

    final int getCanvasRightBorder() {
        return layoutManager.getWidth() - layoutManager.getPaddingRight();
    }

    final int getCanvasBottomBorder() {
        return layoutManager.getHeight();
    }

    final int getCanvasLeftBorder() {
        return layoutManager.getPaddingLeft();
    }

    final int getCanvasTopBorder() {
        return 0;
    }

    final int getCurrentViewPosition() {
        return currentViewPosition;
    }

    final IViewCacheStorage getCacheStorage() {
        return cacheStorage;
    }

    @Override
    public final int getPreviousRowSize() {
        return previousRowSize;
    }

    final void setMaxViewsInRow(@Nullable Integer maxViewsInRow) {
        this.maxViewsInRow = maxViewsInRow;
    }

    /** read view params to memory */
    private void calculateView(View view) {
        currentViewHeight = layoutManager.getDecoratedMeasuredHeight(view);
        currentViewWidth = layoutManager.getDecoratedMeasuredWidth(view);
        currentViewPosition = layoutManager.getPosition(view);
    }

    @Override
    @CallSuper
    /** calculate view positions, view won't be actually added to layout when calling this method
     * @return true if view successfully placed, false if view can't be placed because out of space on screen and have to be recycled */
    public final boolean placeView(View view) {
        calculateView(view);

        if (canNotBePlacedInCurrentRow()) {
            layoutRow();
        }

        if (isFinishedLayouting()) return false;

        rowSize++;
        Rect rect = createViewRect(view);
        rowViews.add(new Pair<>(rect, view));

        return true;
    }

    /** if all necessary view have placed*/
    abstract boolean isFinishedLayouting();

    /** check if we can not add current view to row*/
    @CallSuper
    boolean canNotBePlacedInCurrentRow() {
        return maxViewsInRow!= null && rowSize >= maxViewsInRow;
    }

    /** factory method for Rect, where view will be placed. Creation based on inner layouter parameters */
    abstract Rect createViewRect(View view);

    /** add view to layout manager */
    abstract void addView(View view);

    /** called when layouter ready to add row to canvas. Children could perform normalization actions on created row*/
    abstract void onPreLayout();

    /** called after row have been layouted. Children should prepare new row here. */
    abstract void onAfterLayout();

    @CallSuper
    @Override
    /** Read layouter state from current attached view. We need only last of it, but we can't determine here which is last.
     * Based on characteristics of last attached view, layouter algorithm will be able to continue placing from it.
     * This method have to be called on attaching view*/
    public boolean onAttachView(View view) {
        if (isFinishedLayouting()) return false;
        if (layoutManager.getPosition(view) == 0) {
            Log.d(this.getClass().getSimpleName(), "zero view attached");
        }

        int leftBorderCurrentView = layoutManager.getDecoratedLeft(view);

        if (leftBorderOfPreviouslyAttachedView == null || leftBorderOfPreviouslyAttachedView>= leftBorderCurrentView) {
            //new row, reset row size
            rowSize = 0;
        }

        leftBorderOfPreviouslyAttachedView = leftBorderCurrentView;

        rowSize++;
        layoutManager.attachView(view);
        return true;
    }

    @Override
    /** add views from current row to layout*/
    public final void layoutRow() {
        onPreLayout();

        /** layout pre-calculated row on a recyclerView canvas */
        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;
            View view = rowViewRectPair.second;

            applyChildGravity(view, viewRect, rowTop, rowBottom);
            //add view to layout
            addView(view);
            //layout whole views in a row
            layoutManager.layoutDecorated(view, viewRect.left, viewRect.top, viewRect.right, viewRect.bottom);
        }

        onAfterLayout();

        previousRowSize = rowSize;
        this.rowSize = 0;
        //clear row data
        rowViews.clear();
    }

    private void applyChildGravity(View view, Rect viewRect, int rowTop, int rowBottom) {
        @SpanLayoutChildGravity
        int viewGravity = childGravityResolver.getItemGravity(getLayoutManager().getPosition(view));
        IGravityModifier gravityModifier = gravityModifiersFactory.getGravityModifier(viewGravity);
        gravityModifier.modifyChildRect(rowTop, rowBottom, viewRect);
    }

    ChipsLayoutManager getLayoutManager() {
        return layoutManager;
    }
}
