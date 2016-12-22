package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.IBorder;
import com.beloo.widget.chipslayoutmanager.SpanLayoutChildGravity;
import com.beloo.widget.chipslayoutmanager.gravity.IGravityModifiersFactory;
import com.beloo.widget.chipslayoutmanager.gravity.IRowStrategy;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.ILayoutRowBreaker;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.chipslayoutmanager.gravity.IGravityModifier;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.IFinishingCriteria;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacer;
import com.beloo.widget.chipslayoutmanager.util.AssertionUtils;

/** this class performs measuring, calculation, and placing of views on border (layout manager) according to state criterias */
public abstract class AbstractLayouter implements ILayouter, IBorder {
    private int currentViewWidth;
    private int currentViewHeight;
    private int currentViewPosition;
    List<Pair<Rect, View>> rowViews = new LinkedList<>();
    /** bottom of current row*/
    int viewBottom;
    /** top of current row*/
    int viewTop;

    /** right offset */
    int viewRight;
    /** left offset*/
    int viewLeft;

    private int rowSize = 0;
    private int previousRowSize;

    /** is row completed when {@link #layoutRow()} called*/
    private boolean isRowCompleted;

    ///////////////////////////////////////////////////////////////////////////
    // input dependencies
    ///////////////////////////////////////////////////////////////////////////
    @NonNull
    private ChipsLayoutManager layoutManager;
    @NonNull
    private IViewCacheStorage cacheStorage;
    @NonNull
    private IBorder border;
    @NonNull
    private IChildGravityResolver childGravityResolver;
    @NonNull
    private IFinishingCriteria finishingCriteria;
    @NonNull
    private IPlacer placer;
    @NonNull
    private ILayoutRowBreaker breaker;
    @NonNull
    private IRowStrategy rowStrategy;
    private Set<ILayouterListener> layouterListeners = new HashSet<>();
    @NonNull
    private IGravityModifiersFactory gravityModifiersFactory;
    @NonNull
    private AbstractPositionIterator positionIterator;

    //--- end input dependencies
    AbstractLayouter(Builder builder) {
        //--- read builder
        layoutManager = builder.layoutManager;
        cacheStorage = builder.cacheStorage;
        border = builder.border;
        childGravityResolver = builder.childGravityResolver;
        this.finishingCriteria = builder.finishingCriteria;
        placer = builder.placer;
        this.viewTop = builder.offsetRect.top;
        this.viewBottom = builder.offsetRect.bottom;
        this.viewRight = builder.offsetRect.right;
        this.viewLeft = builder.offsetRect.left;
        this.layouterListeners = builder.layouterListeners;
        this.breaker = builder.breaker;
        this.gravityModifiersFactory = builder.gravityModifiersFactory;
        this.rowStrategy = builder.rowStrategy;
        this.positionIterator = builder.positionIterator;
        //--- end read builder
    }

    void setFinishingCriteria(@NonNull IFinishingCriteria finishingCriteria) {
        this.finishingCriteria = finishingCriteria;
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return positionIterator;
    }

    public boolean isRowCompleted() {
        return isRowCompleted;
    }

    public List<Item> getCurrentRowItems() {
        List<Item> items = new LinkedList<>();
        List<Pair<Rect, View>> mutableRowViews = new LinkedList<>(rowViews);
        if (isReverseOrder()) {
            Collections.reverse(mutableRowViews);
        }
        for (Pair<Rect, View> rowView : mutableRowViews) {
            items.add(new Item(rowView.first, layoutManager.getPosition(rowView.second)));
        }
        return items;
    }

    public final int getCurrentViewPosition() {
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
        for (ILayouterListener layouterListener : layouterListeners) {
            layouterListener.onLayoutRow(this);
        }
    }

    @Override
    public final int getPreviousRowSize() {
        return previousRowSize;
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
            isRowCompleted = true;
            layoutRow();
        }

        if (isFinishedLayouting()) return false;

        rowSize++;
        Rect rect = createViewRect(view);
        rowViews.add(new Pair<>(rect, view));

        return true;
    }

    /** if all necessary view have placed*/
    public final boolean isFinishedLayouting() {
        return finishingCriteria.isFinishedLayouting(this);
    }

    /** check if we can not add current view to row
     * we determine it on the next layouter step, because we need next view size to determine whether it fits in row or not */
    @SuppressWarnings("WeakerAccess")
    public final boolean canNotBePlacedInCurrentRow() {
        return breaker.isRowBroke(this);
    }

    /** factory method for Rect, where view will be placed. Creation based on inner layouter parameters */
    abstract Rect createViewRect(View view);

    /** check whether items in {@link #rowViews} are in reverse order. It is true for backward layouters */
    abstract boolean isReverseOrder();

    /** called when layouter ready to add row to border. Children could perform normalization actions on created row*/
    abstract void onPreLayout();

    /** called after row have been layouted. Children should prepare new row here. */
    abstract void onAfterLayout();

    abstract boolean isAttachedViewFromNewRow(View view);

    abstract void onInterceptAttachView(View view);

    void setPlacer(@NonNull IPlacer placer) {
        this.placer = placer;
    }

    @CallSuper
    @Override
    /** Read layouter state from current attached view. We need only last of it, but we can't determine here which is last.
     * Based on characteristics of last attached view, layouter algorithm will be able to continue placing from it.
     * This method have to be called on attaching view*/
    public final boolean onAttachView(View view) {
        calculateView(view);

        if (isAttachedViewFromNewRow(view)) {
            //new row, reset row size
            notifyLayouterListeners();
            rowSize = 0;
        }

        onInterceptAttachView(view);

        if (isFinishedLayouting()) return false;

        rowSize++;
        layoutManager.attachView(view);
        return true;
    }

    @Override
    /** add views from current row to layout*/
    public final void layoutRow() {
        onPreLayout();

        //apply modifiers to whole row
        if (rowViews.size() > 0) {
            rowStrategy.applyStrategy(this, getCurrentRowItems());
        }

        /** layout pre-calculated row on a recyclerView border */
        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;
            View view = rowViewRectPair.second;

            viewRect = applyChildGravity(view, viewRect);
            //add view to layout
            placer.addView(view);

            //layout whole views in a row
            layoutManager.layoutDecorated(view, viewRect.left, viewRect.top, viewRect.right, viewRect.bottom);
        }

        onAfterLayout();

        notifyLayouterListeners();


        previousRowSize = rowSize;
        //clear row data
        this.rowSize = 0;
        rowViews.clear();
        isRowCompleted = false;
    }

    /** by default items placed and attached to a top of the row.
     * Modify theirs relative positions according to the selected child gravity
     * @return modified rect with applied gravity */
    private Rect applyChildGravity(View view, Rect viewRect) {
        @SpanLayoutChildGravity
        int viewGravity = childGravityResolver.getItemGravity(getLayoutManager().getPosition(view));
        IGravityModifier gravityModifier = gravityModifiersFactory.getGravityModifier(viewGravity);
        return gravityModifier.modifyChildRect(getStartRowBorder(), getEndRowBorder(), viewRect);
    }

    @NonNull
    public ChipsLayoutManager getLayoutManager() {
        return layoutManager;
    }

    /** get count of items inside current row */
    @Override
    public int getRowSize() {
        return rowSize;
    }

    public int getViewTop() {
        return viewTop;
    }

    /** get a start coordinate of row border which is perpendicular to row general extension*/
    public abstract int getStartRowBorder();

    /** get an end coordinate of row border which is perpendicular to row general extension*/
    public abstract int getEndRowBorder();

    @Override
    public Rect getRowRect() {
        return new Rect(getCanvasLeftBorder(), getViewTop(), getCanvasRightBorder(), getViewBottom());
    }

    public int getViewBottom() {
        return viewBottom;
    }

    final Rect getOffsetRect() {
        return new Rect(viewLeft, viewTop, viewRight, viewBottom);
    }

    public final int getViewLeft() {
        return viewLeft;
    }

    public final int getViewRight() {
        return viewRight;
    }

    public final int getCurrentViewWidth() {
        return currentViewWidth;
    }

    public final int getCurrentViewHeight() {
        return currentViewHeight;
    }

    public abstract int getRowLength();

    public abstract static class Builder {
        private ChipsLayoutManager layoutManager;
        private IViewCacheStorage cacheStorage;
        private IBorder border;
        private IChildGravityResolver childGravityResolver;
        private IFinishingCriteria finishingCriteria;
        private IPlacer placer;
        private ILayoutRowBreaker breaker;
        private Rect offsetRect;
        private HashSet<ILayouterListener> layouterListeners = new HashSet<>();
        private IGravityModifiersFactory gravityModifiersFactory;
        private IRowStrategy rowStrategy;
        private AbstractPositionIterator positionIterator;

        Builder() {}

        @SuppressWarnings("WeakerAccess")
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

        @NonNull
        final Builder cacheStorage(@NonNull IViewCacheStorage cacheStorage) {
            this.cacheStorage = cacheStorage;
            return this;
        }

        @NonNull
        Builder rowStrategy(IRowStrategy rowStrategy) {
            this.rowStrategy = rowStrategy;
            return this;
        }

        @NonNull
        final Builder canvas(@NonNull IBorder border) {
            this.border = border;
            return this;
        }

        @NonNull
        final Builder gravityModifiersFactory(@NonNull IGravityModifiersFactory gravityModifiersFactory) {
            this.gravityModifiersFactory = gravityModifiersFactory;
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

        @SuppressWarnings("unused")
        @NonNull
        final Builder addLayouterListener(@Nullable ILayouterListener layouterListener) {
            if (layouterListener != null) {
                layouterListeners.add(layouterListener);
            }
            return this;
        }

        @NonNull
        final Builder breaker(@NonNull ILayoutRowBreaker breaker) {
            AssertionUtils.assertNotNull(breaker, "breaker shouldn't be null");
            this.breaker = breaker;
            return this;
        }

        @NonNull
        final Builder addLayouterListeners(@NonNull List<ILayouterListener> layouterListeners) {
            this.layouterListeners.addAll(layouterListeners);
            return this;
        }

        @NonNull
        public Builder positionIterator(AbstractPositionIterator positionIterator) {
            this.positionIterator = positionIterator;
            return this;
        }

        @NonNull
        protected abstract AbstractLayouter createLayouter();

        public final AbstractLayouter build() {
            if (layoutManager == null)
                throw new IllegalStateException("layoutManager can't be null, call #layoutManager()");

            if (breaker == null)
                throw new IllegalStateException("breaker can't be null, call #breaker()");

            if (border == null)
                throw new IllegalStateException("border can't be null, call #border()");

            if (cacheStorage == null)
                throw new IllegalStateException("cacheStorage can't be null, call #cacheStorage()");

            if (rowStrategy == null)
                throw new IllegalStateException("rowStrategy can't be null, call #rowStrategy()");

            if (offsetRect == null)
                throw new IllegalStateException("offsetRect can't be null, call #offsetRect()");

            if (finishingCriteria == null)
                throw new IllegalStateException("finishingCriteria can't be null, call #finishingCriteria()");

            if (placer == null)
                throw new IllegalStateException("placer can't be null, call #placer()");

            if (gravityModifiersFactory == null)
                throw new IllegalStateException("gravityModifiersFactory can't be null, call #gravityModifiersFactory()");

            if (childGravityResolver == null)
                throw new IllegalStateException("childGravityResolver can't be null, call #childGravityResolver()");

            if (positionIterator == null)
                throw new IllegalStateException("positionIterator can't be null, call #positionIterator()");

            return createLayouter();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // border delegate
    ///////////////////////////////////////////////////////////////////////////

    public final int getCanvasRightBorder() {
        return border.getCanvasRightBorder();
    }

    public final int getCanvasBottomBorder() {
        return border.getCanvasBottomBorder();
    }

    public final int getCanvasLeftBorder() {
        return border.getCanvasLeftBorder();
    }

    public final int getCanvasTopBorder() {
        return border.getCanvasTopBorder();
    }

}
