package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpanLayoutChildGravity;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.GravityModifiersFactory;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.chipslayoutmanager.gravity.IGravityModifier;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.IFinishingCriteria;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacer;

public abstract class AbstractLayouter implements ILayouter, ICanvas {
    int currentViewWidth;
    int currentViewHeight;
    private int currentViewPosition;
    List<Pair<Rect, View>> rowViews = new LinkedList<>();
    /** bottom of current row*/
    int rowBottom;
    /** top of current row*/
    int rowTop;

    /** right offset */
    int viewRight;
    /** left offset*/
    int viewLeft;

    @Nullable
    private Integer leftBorderOfPreviouslyAttachedView = null;

    private int rowSize = 0;
    private int previousRowSize;

    //--- input dependencies
    private ChipsLayoutManager layoutManager;
    private IViewCacheStorage cacheStorage;
    private ICanvas canvas;
    @NonNull
    private IChildGravityResolver childGravityResolver;
    @NonNull
    private IFinishingCriteria finishingCriteria;
    @NonNull
    private IPlacer placer;

    /** Max items in row restriction. Layout of row should be stopped when this count of views reached*/
    @Nullable
    private Integer maxViewsInRow = null;
    //--- end input dependencies

    private AbstractPositionIterator positionIterator;

    private GravityModifiersFactory gravityModifiersFactory = new GravityModifiersFactory();

    private List<ILayouterListener> layouterListeners = new LinkedList<>();

    AbstractLayouter(Builder builder) {
        //--- read builder
        layoutManager = builder.layoutManager;
        cacheStorage = builder.cacheStorage;
        canvas = builder.canvas;
        childGravityResolver = builder.childGravityResolver;
        setFinishingCriteria(builder.finishingCriteria);
        placer = builder.placer;
        this.rowTop = builder.offsetRect.top;
        this.rowBottom = builder.offsetRect.bottom;
        this.viewRight = builder.offsetRect.right;
        this.viewLeft = builder.offsetRect.left;
        this.maxViewsInRow = builder.maxCountInRow;
        this.layouterListeners = builder.layouterListeners;
        //--- end read builder

        positionIterator = createPositionIterator();
    }

    void setFinishingCriteria(@NonNull IFinishingCriteria finishingCriteria) {
        this.finishingCriteria = finishingCriteria;
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return positionIterator;
    }

    public final int getCanvasRightBorder() {
        return canvas.getCanvasRightBorder();
    }

    public final int getCanvasBottomBorder() {
        return canvas.getCanvasBottomBorder();
    }

    public final int getCanvasLeftBorder() {
        return canvas.getCanvasLeftBorder();
    }

    public final int getCanvasTopBorder() {
        return canvas.getCanvasTopBorder();
    }

    public List<Item> getCurrentRowItems() {
        List<Item> items = new LinkedList<>();
        for (Pair<Rect, View> rowView : rowViews) {
            items.add(new Item(rowView.first, layoutManager.getPosition(rowView.second)));
        }
        return items;
    }

    final int getCurrentViewPosition() {
        return currentViewPosition;
    }

    final IViewCacheStorage getCacheStorage() {
        return cacheStorage;
    }

    public void addLayouterListener(ILayouterListener layouterListener) {
        if (layouterListener != null)
            layouterListeners.add(layouterListener);
    }

    @Override
    public void removeLayouterListener(ILayouterListener layouterListener) {
        layouterListeners.remove(layouterListener);
    }

    private void notifyLayouterListeners() {
        if (rowSize > 0) {
            for (ILayouterListener layouterListener : layouterListeners) {
                layouterListener.onLayoutRow(this);
            }
        }
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
        layoutManager.measureChildWithMargins(view, 0, 0);
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
    boolean isFinishedLayouting() {
        return finishingCriteria.isFinishedLayouting(this);
    }

    /** check if we can not add current view to row*/
    @CallSuper
    boolean canNotBePlacedInCurrentRow() {
        return maxViewsInRow!= null && rowSize >= maxViewsInRow;
    }

    /** factory method for Rect, where view will be placed. Creation based on inner layouter parameters */
    abstract Rect createViewRect(View view);

    /** called when layouter ready to add row to canvas. Children could perform normalization actions on created row*/
    abstract void onPreLayout();

    /** called after row have been layouted. Children should prepare new row here. */
    abstract void onAfterLayout();

    abstract AbstractPositionIterator createPositionIterator();

    void setPlacer(@NonNull IPlacer placer) {
        this.placer = placer;
    }

    @CallSuper
    @Override
    /** Read layouter state from current attached view. We need only last of it, but we can't determine here which is last.
     * Based on characteristics of last attached view, layouter algorithm will be able to continue placing from it.
     * This method have to be called on attaching view*/
    public boolean onAttachView(View view) {
        if (isFinishedLayouting()) return false;

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
            placer.addView(view);

            if (getCurrentViewPosition() == 0) {
                Log.d("abstract layouter", "zero view rect = " + viewRect);
            }

            //layout whole views in a row
            layoutManager.layoutDecorated(view, viewRect.left, viewRect.top, viewRect.right, viewRect.bottom);
        }

        notifyLayouterListeners();

        onAfterLayout();

        previousRowSize = rowSize;
        this.rowSize = 0;
        //clear row data
        rowViews.clear();
    }

    /** by default items placed and attached to a top of the row.
     * Modify theirs relative positions according to the selected child gravity */
    private void applyChildGravity(View view, Rect viewRect, int rowTop, int rowBottom) {
        @SpanLayoutChildGravity
        int viewGravity = childGravityResolver.getItemGravity(getLayoutManager().getPosition(view));
        IGravityModifier gravityModifier = gravityModifiersFactory.getGravityModifier(viewGravity);
        gravityModifier.modifyChildRect(rowTop, rowBottom, viewRect);
    }

    public ChipsLayoutManager getLayoutManager() {
        return layoutManager;
    }

    @Override
    public int getRowSize() {
        return rowSize;
    }

    @Override
    public int getRowTop() {
        return rowTop;
    }

    @Override
    public Rect getRowRect() {
        return new Rect(getCanvasLeftBorder(), rowTop, getCanvasRightBorder(), getRowBottom());
    }

    @Override
    public int getRowBottom() {
        return rowBottom;
    }

    Rect getOffsetRect() {
        return new Rect(viewLeft, rowTop, viewRight, rowBottom);
    }

    int getLeftOffset() {
        return viewRight;
    }

    int getRightOffset() {
        return viewLeft;
    }

    public abstract static class Builder {
        private ChipsLayoutManager layoutManager;
        private IViewCacheStorage cacheStorage;
        private ICanvas canvas;
        private IChildGravityResolver childGravityResolver;
        private IFinishingCriteria finishingCriteria;
        private IPlacer placer;
        private Rect offsetRect;
        private Integer maxCountInRow;
        private List<ILayouterListener> layouterListeners = new LinkedList<>();

        Builder() {}

        @NonNull
        public Builder offsetRect(@NonNull Rect offsetRect) {
            this.offsetRect = offsetRect;
            return this;
        }

        @NonNull
        public final Builder layoutManager(@NonNull ChipsLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
            return this;
        }

        Builder maxCountInRow(Integer maxCountInRow) {
            this.maxCountInRow = maxCountInRow;
            return this;
        }

        @NonNull
        final Builder cacheStorage(@NonNull IViewCacheStorage cacheStorage) {
            this.cacheStorage = cacheStorage;
            return this;
        }

        @NonNull
        final Builder canvas(@NonNull ICanvas canvas) {
            this.canvas = canvas;
            return this;
        }

        @NonNull
        final Builder childGravityResolver(@NonNull IChildGravityResolver childGravityResolver) {
            this.childGravityResolver = childGravityResolver;
            return this;
        }

        @NonNull
        final Builder finishingCriteria(@NonNull IFinishingCriteria finishingCriteria) {
            this.finishingCriteria = finishingCriteria;
            return this;
        }

        @NonNull
        public final Builder placer(@NonNull IPlacer placer) {
            this.placer = placer;
            return this;
        }

        @NonNull
        final Builder addLayouterListener(@Nullable ILayouterListener layouterListener) {
            if (layouterListener != null) {
                layouterListeners.add(layouterListener);
            }
            return this;
        }

        @NonNull
        final Builder addLayouterListeners(@NonNull List<ILayouterListener> layouterListeners) {
            this.layouterListeners.addAll(layouterListeners);
            return this;
        }

        @NonNull
        public abstract AbstractLayouter build();
    }
}
