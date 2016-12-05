package com.beloo.widget.chipslayoutmanager;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;
import com.beloo.widget.chipslayoutmanager.anchor.IAnchorFactory;
import com.beloo.widget.chipslayoutmanager.layouter.ColumnsStateFactory;
import com.beloo.widget.chipslayoutmanager.layouter.IMeasureSupporter;
import com.beloo.widget.chipslayoutmanager.layouter.IStateFactory;
import com.beloo.widget.chipslayoutmanager.layouter.MeasureSupporter;
import com.beloo.widget.chipslayoutmanager.layouter.RowsStateFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.EmptyRowBreaker;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.IRowBreaker;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.cache.ViewCacheFactory;
import com.beloo.widget.chipslayoutmanager.gravity.CenterChildGravity;
import com.beloo.widget.chipslayoutmanager.gravity.CustomGravityResolver;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.chipslayoutmanager.layouter.LayouterFactory;
import com.beloo.widget.chipslayoutmanager.layouter.AbstractPositionIterator;
import com.beloo.widget.chipslayoutmanager.layouter.ILayouter;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.AbstractCriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.InfiniteCriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.PlacerFactory;
import com.beloo.widget.chipslayoutmanager.logger.IAdapterActionsLogger;
import com.beloo.widget.chipslayoutmanager.logger.IFillLogger;
import com.beloo.widget.chipslayoutmanager.logger.IPredictiveAnimationsLogger;
import com.beloo.widget.chipslayoutmanager.logger.IScrollingLogger;
import com.beloo.widget.chipslayoutmanager.logger.LoggerFactory;
import com.beloo.widget.chipslayoutmanager.util.AssertionUtils;

import java.util.List;

public class ChipsLayoutManager extends RecyclerView.LayoutManager implements IChipsLayoutManagerContract, IStateHolder, IPositionsContract {
    ///////////////////////////////////////////////////////////////////////////
    // orientation types
    ///////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("WeakerAccess")
    public static final int HORIZONTAL = 1;
    @SuppressWarnings("WeakerAccess")
    public static final int VERTICAL = 2;

    ///////////////////////////////////////////////////////////////////////////
    // row strategy types
    ///////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("WeakerAccess")
    public static final int STRATEGY_DEFAULT = 1;
    @SuppressWarnings("WeakerAccess")
    public static final int STRATEGY_FILL_VIEW = 2;
    @SuppressWarnings("WeakerAccess")
    public static final int STRATEGY_FILL_SPACE = 4;
    @SuppressWarnings("WeakerAccess")
    public static final int STRATEGY_CENTER = 5;

    ///////////////////////////////////////////////////////////////////////////
    // inner constants
    ///////////////////////////////////////////////////////////////////////////
    private static final String TAG = ChipsLayoutManager.class.getSimpleName();
    private static final int INT_ROW_SIZE_APPROXIMATELY_FOR_CACHE = 10;
    private static final int APPROXIMATE_ADDITIONAL_ROWS_COUNT = 5;
    /**
     * coefficient to support fast scrolling, caching views only for one row may not be enough
     */
    private static final float FAST_SCROLLING_COEFFICIENT = 2;

    /** delegate which represents available canvas for drawing views according to layout*/
    private ICanvas canvas = new Square(this);

    /** iterable over views added to RecyclerView */
    private ChildViewsIterable childViews = new ChildViewsIterable(this);

    ///////////////////////////////////////////////////////////////////////////
    // contract parameters
    ///////////////////////////////////////////////////////////////////////////
    /** determine gravity of child inside row*/
    private IChildGravityResolver childGravityResolver;
    private boolean isScrollingEnabledContract = true;
    /** strict restriction of max count of views in particular row */
    private Integer maxViewsInRow = null;
    /** determines whether LM should break row from view position */
    private IRowBreaker rowBreaker = new EmptyRowBreaker();
    //--- end contract parameters
    @Orientation
    /** layoutOrientation of layout. Could have HORIZONTAL or VERTICAL style */
    private int layoutOrientation = HORIZONTAL;
    @RowStrategy
    private int rowStrategy = STRATEGY_DEFAULT;
    private boolean isStrategyAppliedWithLastRow;

    ///////////////////////////////////////////////////////////////////////////
    // cache
    ///////////////////////////////////////////////////////////////////////////

    /** store positions of placed view to know when LM should break row while moving back
     * this cache mostly needed to place views when scrolling down to the same places, where they have been previously */
    private IViewCacheStorage viewPositionsStorage;

    /**
     * when scrolling reached this position {@link ChipsLayoutManager} is able to restore items layout according to cached items with positions above.
     * That layout would exactly correspond to current item view situation
     */
    @Nullable
    private Integer cacheNormalizationPosition = null;

    /**
     * store detached views to probably reattach it if them still visible.
     * Used while scrolling
     */
    private SparseArray<View> viewCache = new SparseArray<>();

    /**
     * storing state due layoutOrientation changes
     */
    private ParcelableContainer container = new ParcelableContainer();

    ///////////////////////////////////////////////////////////////////////////
    // loggers
    ///////////////////////////////////////////////////////////////////////////
    private IFillLogger logger;
    private IAdapterActionsLogger adapterActionsLogger;
    private IPredictiveAnimationsLogger predictiveAnimationsLogger;
    private IScrollingLogger scrollingLogger;
    //--- end loggers

    /**
     * is layout in RTL mode. Variable needed to detect mode changes
     */
    private boolean isLayoutRTL = false;

    /**
     * current device layoutOrientation
     */
    @DeviceOrientation
    private int orientation;

    ///////////////////////////////////////////////////////////////////////////
    // borders
    ///////////////////////////////////////////////////////////////////////////

    /**
     * stored current anchor view due to scroll state changes
     */
    private AnchorViewState anchorView;

    /**
     * highest view in layout. Have always actual value, because it set in {@link #onLayoutChildren}
     */
    private View topView;
    /**
     * lowest view in layout. Have always actual value, because it set in {@link #onLayoutChildren}
     */
    private View bottomView;

    /**
     * The view have placed in the closest to the left border. Have always actual value, because it set in {@link #onLayoutChildren}
     */
    private View leftView;

    /** The view have placed in the closest to the right border. Have always actual value, because it set in {@link #onLayoutChildren} */
    private View rightView;

    /** minimal position visible on screen*/
    private Integer minPositionOnScreen;
    private Integer maxPositionOnScreen;

    private boolean isFirstItemAdded;

    ///////////////////////////////////////////////////////////////////////////
    // state-dependent
    ///////////////////////////////////////////////////////////////////////////
    /** factory for state-dependent layouter factories*/
    private IStateFactory stateFactory;

    /** manage auto-measuring */
    private IMeasureSupporter measureSupporter;

    /** factory which could retrieve anchorView on which layouting based*/
    private IAnchorFactory anchorFactory;

    /** manage scrolling of layout manager according to current state */
    private IScrollingController scrollingController;
    //--- end state-dependent vars

    /* in pre-layouter drawing we need item count with items will be actually deleted to pre-draw appearing items properly
    * buf value*/
    private int deletingItemsOnScreenCount;

    /** factory for placers factories*/
    private PlacerFactory placerFactory = new PlacerFactory(this);

    private boolean isAfterPreLayout;

    private ChipsLayoutManager(Context context) {
        @DeviceOrientation
        int orientation = context.getResources().getConfiguration().orientation;
        this.orientation = orientation;

        LoggerFactory loggerFactory = new LoggerFactory();
        logger = loggerFactory.getFillLogger(viewCache);
        adapterActionsLogger = loggerFactory.getAdapterActionsLogger();
        predictiveAnimationsLogger = loggerFactory.getPredictiveAnimationsLogger();
        scrollingLogger = loggerFactory.getScrollingLogger();

        viewPositionsStorage = new ViewCacheFactory(this).createCacheStorage();
        measureSupporter = new MeasureSupporter(this);
        setAutoMeasureEnabled(true);
    }

    public static Builder newBuilder(Context context) {
        return new ChipsLayoutManager(context).new StrategyBuilder();
    }

    public IChildGravityResolver getChildGravityResolver() {
        return childGravityResolver;
    }

    /** use it to strictly disable scrolling.
     * If scrolling enabled it would be disabled in case all items fit on the screen */
    public void setScrollingEnabledContract(boolean isEnabled) {
        isScrollingEnabledContract = isEnabled;
    }

    /**
     * change max count of row views in runtime
     */
    @SuppressWarnings("unused")
    public void setMaxViewsInRow(@IntRange(from = 1) Integer maxViewsInRow) {
        if (maxViewsInRow < 1)
            throw new IllegalArgumentException("maxViewsInRow should be positive, but is = " + maxViewsInRow);
        this.maxViewsInRow = maxViewsInRow;
        onRuntimeLayoutChanges();
    }

    private void onRuntimeLayoutChanges() {
        cacheNormalizationPosition = 0;
        viewPositionsStorage.purge();
        requestLayoutWithAnimations();
    }

    public Integer getMaxViewsInRow() {
        return maxViewsInRow;
    }

    public IRowBreaker getRowBreaker() {
        return rowBreaker;
    }

    @RowStrategy
    public int getRowStrategyType() {
        return rowStrategy;
    }

    public boolean isStrategyAppliedWithLastRow() {
        return isStrategyAppliedWithLastRow;
    }

    @RestrictTo(RestrictTo.Scope.GROUP_ID)
    public IViewCacheStorage getViewPositionsStorage() {
        return viewPositionsStorage;
    }

    @RestrictTo(RestrictTo.Scope.GROUP_ID)
    public ICanvas getCanvas() {
        return canvas;
    }

    /**
     * perform changing layout with playing RecyclerView animations
     */
    private void requestLayoutWithAnimations() {
        postOnAnimation(new Runnable() {
            @Override
            public void run() {
                ChipsLayoutManager.super.requestLayout();
                requestSimpleAnimationsInNextLayout();
            }
        });
    }

    //create decorator if any other builders would be added
    @SuppressWarnings("WeakerAccess")
    public class StrategyBuilder extends Builder {

        /** @param withLastRow true, if row strategy should be applied to last row.
         * @see Builder#setRowStrategy(int) */
        @SuppressWarnings("unused")
        public Builder withLastRow(boolean withLastRow) {
            ChipsLayoutManager.this.isStrategyAppliedWithLastRow = withLastRow;
            return this;
        }

    }

    @SuppressWarnings("WeakerAccess")
    public class Builder {

        @SpanLayoutChildGravity
        private Integer gravity;

        private Builder() {
        }

        /**
         * set vertical gravity in a row for all children. Default = CENTER_VERTICAL
         */
        @SuppressWarnings({"unused", "WeakerAccess"})
        public Builder setChildGravity(@SpanLayoutChildGravity int gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * set gravity resolver in case you need special gravity for items. This method have priority over {@link #setChildGravity(int)}
         */
        @SuppressWarnings("unused")
        public Builder setGravityResolver(@NonNull IChildGravityResolver gravityResolver) {
            AssertionUtils.assertNotNull(gravityResolver, "gravity resolver couldn't be null");
            childGravityResolver = gravityResolver;
            return this;
        }

        /**
         * strictly disable scrolling if needed
         */
        @SuppressWarnings("unused")
        public Builder setScrollingEnabled(boolean isEnabled) {
            ChipsLayoutManager.this.setScrollingEnabledContract(isEnabled);
            return this;
        }

        /** row strategy for views in completed row.
         * Any row has some space left, where is impossible to place the next view, because that space is too small.
         * But we could distribute that space for available views in that row
         * @param rowStrategy is a mode of distribution left space<br/>
         * {@link #STRATEGY_DEFAULT} is used by default. Left space is placed at the end of the row.<br/>
         * {@link #STRATEGY_FILL_VIEW} available space is distributed among views<br/>
         * {@link #STRATEGY_FILL_SPACE} available space is distributed among spaces between views, start & end views are docked to a nearest border<br/>
         * {@link #STRATEGY_CENTER} available space is distributed among spaces between views, start & end spaces included. Views are placed in center of canvas<br/>
         * <br/>
         * In such layouts by default last row isn't considered completed. So strategy isn't applied for last row.<br/>
         * But you can also enable opposite behaviour.
         * @see StrategyBuilder#withLastRow(boolean)
         */
        @SuppressWarnings("unused")
        public StrategyBuilder setRowStrategy(@RowStrategy int rowStrategy) {
            ChipsLayoutManager.this.rowStrategy = rowStrategy;
            return (StrategyBuilder) this;
        }

        /**
         * set maximum possible count of views in row
         */
        @SuppressWarnings("unused")
        public Builder setMaxViewsInRow(@IntRange(from = 1) int maxViewsInRow) {
            if (maxViewsInRow < 1)
                throw new IllegalArgumentException("maxViewsInRow should be positive, but is = " + maxViewsInRow);
            ChipsLayoutManager.this.maxViewsInRow = maxViewsInRow;
            return this;
        }

        /** @param breaker override to determine whether ChipsLayoutManager should breaks row due to position of view. */
        @SuppressWarnings("unused")
        public Builder setRowBreaker(@NonNull IRowBreaker breaker) {
            AssertionUtils.assertNotNull(breaker, "breaker couldn't be null");
            ChipsLayoutManager.this.rowBreaker = breaker;
            return this;
        }

        /** @param orientation of layout manager. Could be {@link #HORIZONTAL} or {@link #VERTICAL}
         * {@link #HORIZONTAL} by default */
        public Builder setOrientation(@Orientation int orientation) {
            if (orientation != HORIZONTAL && orientation != VERTICAL) {
                return this;
            }
            ChipsLayoutManager.this.layoutOrientation = orientation;
            return this;
        }

        /**
         * create SpanLayoutManager
         */
        public ChipsLayoutManager build() {
            // setGravityResolver always have priority
            if (childGravityResolver == null) {
                if (gravity != null) {
                    childGravityResolver = new CustomGravityResolver(gravity);
                } else {
                    childGravityResolver = new CenterChildGravity();
                }
            }

            stateFactory = layoutOrientation == HORIZONTAL ? new RowsStateFactory(ChipsLayoutManager.this) : new ColumnsStateFactory(ChipsLayoutManager.this);
            anchorFactory = stateFactory.anchorFactory();
            scrollingController = stateFactory.scrollingController();
            anchorView = anchorFactory.createNotFound();

            return ChipsLayoutManager.this;
        }

    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        container = (ParcelableContainer) state;
        anchorView = anchorFactory.createNotFound();
        anchorView.setPosition(container.getAnchorPosition());

        viewPositionsStorage.onRestoreInstanceState(container.getPositionsCache(orientation));
        cacheNormalizationPosition = container.getNormalizationPosition(orientation);

        Log.d(TAG, "RESTORE. last cache position before cleanup = " + viewPositionsStorage.getLastCachePosition());
        if (cacheNormalizationPosition != null) {
            viewPositionsStorage.purgeCacheFromPosition(cacheNormalizationPosition);
        }
        viewPositionsStorage.purgeCacheFromPosition(container.getAnchorPosition());
        Log.d(TAG, "RESTORE. anchor position =" + container.getAnchorPosition());
        Log.d(TAG, "RESTORE. layoutOrientation = " + orientation + " normalizationPos = " + cacheNormalizationPosition);
        Log.d(TAG, "RESTORE. last cache position = " + viewPositionsStorage.getLastCachePosition());
    }

    @Override
    public Parcelable onSaveInstanceState() {

        //store only position on anchor. Rect of anchor will be invalidated
        int anchorPosition = anchorView.getPosition();
        container.putAnchorPosition(anchorPosition);
        container.putPositionsCache(orientation, viewPositionsStorage.onSaveInstanceState());
        Log.d(TAG, "STORE. last cache position =" + viewPositionsStorage.getLastCachePosition());

        Integer storedNormalizationPosition = cacheNormalizationPosition != null ? cacheNormalizationPosition : viewPositionsStorage.getLastCachePosition();

        Log.d(TAG, "STORE. layoutOrientation = " + orientation + " normalizationPos = " + storedNormalizationPosition);

        container.putNormalizationPosition(orientation, storedNormalizationPosition);

        return container;
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // positions contract
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the adapter position of the first visible view. This position does not include
     * adapter changes that were dispatched after the last layout pass.
     * If RecyclerView has item decorators, they will be considered in calculations as well.
     * <p>
     * LayoutManager may pre-cache some views that are not necessarily visible. Those views
     * are ignored in this method.
     *
     * @return The adapter position of the first visible item or {@link RecyclerView#NO_POSITION} if
     * there aren't any visible items.
     * @see #findFirstCompletelyVisibleItemPosition()
     * @see #findLastVisibleItemPosition()
     */
    @Override
    public int findFirstVisibleItemPosition() {
        if (getChildCount() == 0)
            return RecyclerView.NO_POSITION;
        return minPositionOnScreen;
    }

    /**
     * Returns the adapter position of the first fully visible view. This position does not include
     * adapter changes that were dispatched after the last layout pass.
     *
     * @return The adapter position of the first fully visible item or
     * {@link RecyclerView#NO_POSITION} if there aren't any visible items.
     * @see #findFirstVisibleItemPosition()
     * @see #findLastCompletelyVisibleItemPosition()
     */
    @Override
    public int findFirstCompletelyVisibleItemPosition() {
        for (View view : childViews) {
            Rect rect = canvas.getViewRect(view);
            if (!canvas.isFullyVisible(rect)) continue;
            if (canvas.isInside(rect)) {
                return getPosition(view);
            }
        }

        return RecyclerView.NO_POSITION;
    }

    /**
     * Returns the adapter position of the last visible view. This position does not include
     * adapter changes that were dispatched after the last layout pass.
     * If RecyclerView has item decorators, they will be considered in calculations as well.
     * <p>
     * LayoutManager may pre-cache some views that are not necessarily visible. Those views
     * are ignored in this method.
     *
     * @return The adapter position of the last visible view or {@link RecyclerView#NO_POSITION} if
     * there aren't any visible items.
     * @see #findLastCompletelyVisibleItemPosition()
     * @see #findFirstVisibleItemPosition()
     */
    @Override
    public int findLastVisibleItemPosition() {
        if (getChildCount() == 0)
            return RecyclerView.NO_POSITION;
        return maxPositionOnScreen;
    }

    /**
     * Returns the adapter position of the last fully visible view. This position does not include
     * adapter changes that were dispatched after the last layout pass.
     *
     *  @return The adapter position of the last fully visible view or
     * {@link RecyclerView#NO_POSITION} if there aren't any visible items.
     * @see #findLastVisibleItemPosition()
     * @see #findFirstCompletelyVisibleItemPosition()
     */
    @Override
    public int findLastCompletelyVisibleItemPosition() {

        for (int i = getChildCount() - 1; i >=0; i--) {
            View view = getChildAt(i);
            Rect rect = canvas.getViewRect(view);
            if (!canvas.isFullyVisible(rect)) continue;
            if (canvas.isInside(view)) {
                return getPosition(view);
            }
        }

        return RecyclerView.NO_POSITION;
    }

    ///////////////////////////////////////////////////////////////////////////
    // orientation
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return true if RTL mode enabled in RecyclerView
     */
    public boolean isLayoutRTL() {
        return getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    @Override
    @Orientation
    public int layoutOrientation() {
        return layoutOrientation;
    }

    @Override
    public int getItemCount() {
        //in pre-layouter drawing we need item count with items will be actually deleted to pre-draw appearing items properly
        return super.getItemCount() + deletingItemsOnScreenCount;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //We have nothing to show for an empty data set but clear any existing views
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        predictiveAnimationsLogger.logState(state);

        if (isLayoutRTL() != isLayoutRTL) {
            //if layout direction changed programmatically we should clear anchors
            isLayoutRTL = isLayoutRTL();
            //so detach all views before we start searching for anchor view
            detachAndScrapAttachedViews(recycler);

        }

        calcRecyclerCacheSize(recycler);

        if (!state.isPreLayout()) {
            detachAndScrapAttachedViews(recycler);

            //we perform layouting stage from scratch, so cache will be rebuilt soon, we could purge it and avoid unnecessary normalization
            viewPositionsStorage.purgeCacheFromPosition(anchorView.getPosition());
            if (cacheNormalizationPosition != null && anchorView.getPosition() <= cacheNormalizationPosition) {
                cacheNormalizationPosition = null;
            }

            /** In case some moving views
             * we should place it at layout to support predictive animations
             * we can't place all possible moves on theirs real place, because concrete layout position of particular view depends on placing of previous views
             * and there could be moving from 0 position to 10k. But it is preferably to place nearest moved view to real positions to make moving more natural
             * like moving from 0 position to 15 for example, where user could scroll fast and check
             * so we fill additional rows to cover nearest moves
             */
            AbstractCriteriaFactory criteriaFactory = stateFactory.createDefaultFinishingCriteriaFactory();
            criteriaFactory.setAdditionalRowsCount(APPROXIMATE_ADDITIONAL_ROWS_COUNT);

            LayouterFactory layouterFactory = stateFactory.createLayouterFactory(criteriaFactory, placerFactory.createRealPlacerFactory());

            fill(recycler, layouterFactory, anchorView, isAfterPreLayout);
            isAfterPreLayout = false;
        } else {
            //inside pre-layout stage. It is called when item animation reconstruction will be played
            //it is NOT called on layoutOrientation changes

            int additionalLength = calcDisappearingViewsLength(recycler);
            predictiveAnimationsLogger.heightOfCanvas(this);
            predictiveAnimationsLogger.onSummarizedDeletingItemsHeightCalculated(additionalLength);
            anchorView = anchorFactory.getAnchor();
            anchorFactory.resetRowCoordinates(anchorView);
            Log.w(TAG, "anchor state in pre-layout = " + anchorView);
            detachAndScrapAttachedViews(recycler);

            //in case removing draw additional rows to show predictive animations for appearing views
            AbstractCriteriaFactory criteriaFactory = stateFactory.createDefaultFinishingCriteriaFactory();
            criteriaFactory.setAdditionalRowsCount(APPROXIMATE_ADDITIONAL_ROWS_COUNT);
            criteriaFactory.setAdditionalLength(additionalLength);

            LayouterFactory layouterFactory = stateFactory.createLayouterFactory(criteriaFactory, placerFactory.createRealPlacerFactory());

            fill(recycler, layouterFactory, anchorView, false);
            isAfterPreLayout = true;
        }

        deletingItemsOnScreenCount = 0;

        if (!state.isMeasuring()) {
            measureSupporter.onSizeChanged();
        }

        //we should re-layout if previous anchor was removed or moved.
        anchorView = anchorFactory.getAnchor();
        if (!anchorView.isNotFoundState()) {
            Rect rect = anchorView.getAnchorViewRect();
            rect.left = getPaddingLeft();
            rect.right = getPaddingRight();
        }
        if (anchorFactory.normalize(anchorView)) {
            requestLayoutWithAnimations();
        }
    }

    /** layout disappearing view to support predictive animations */
    private void layoutDisappearingViews(RecyclerView.Recycler recycler, ILayouter upLayouter, ILayouter downLayouter) {

        ICriteriaFactory criteriaFactory = new InfiniteCriteriaFactory();
        LayouterFactory layouterFactory = stateFactory.createLayouterFactory(criteriaFactory, placerFactory.createDisappearingPlacerFactory());

        DisappearingViewsContainer disappearingViews = getDisappearingViews(recycler);

        if (disappearingViews.size() > 0) {
            Log.d("disappearing views", "count = " + disappearingViews.size());
            Log.d("fill disappearing views", "");
            downLayouter = layouterFactory.buildForwardLayouter(downLayouter);

            //we should layout disappearing views left somewhere, just continue layout them in current layouter
            for (int i = 0; i< disappearingViews.forwardViews.size(); i++) {
                int position = disappearingViews.forwardViews.keyAt(i);
                downLayouter.placeView(recycler.getViewForPosition(position));
            }
            //layout last row
            downLayouter.layoutRow();

            upLayouter = layouterFactory.buildBackwardLayouter(upLayouter);
            //we should layout disappearing views left somewhere, just continue layout them in current layouter
            for (int i = 0; i< disappearingViews.backwardViews.size(); i++) {
                int position = disappearingViews.backwardViews.keyAt(i);
                upLayouter.placeView(recycler.getViewForPosition(position));
            }

            //layout last row
            upLayouter.layoutRow();
        }
    }

    private class DisappearingViewsContainer {
        private SparseArray<View> backwardViews = new SparseArray<>();
        private SparseArray<View> forwardViews = new SparseArray<>();

        int size() {
            return backwardViews.size() + forwardViews.size();
        }
    }

    /** @return views which moved from screen, but not deleted*/
    private DisappearingViewsContainer getDisappearingViews(RecyclerView.Recycler recycler) {
        final List<RecyclerView.ViewHolder> scrapList = recycler.getScrapList();
        DisappearingViewsContainer container = new DisappearingViewsContainer();

        for (RecyclerView.ViewHolder holder : scrapList) {
            final View child = holder.itemView;
            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (!lp.isItemRemoved()) {
                if (lp.getViewAdapterPosition() < minPositionOnScreen) {
                    container.backwardViews.put(lp.getViewAdapterPosition(), child);
                } else if (lp.getViewAdapterPosition() > maxPositionOnScreen) {
                    container.forwardViews.put(lp.getViewAdapterPosition(), child);
                }
            }
        }

        return container;
    }
    /** during pre-layout calculate approximate height which will be free after moving items offscreen (removed or moved)
     * @return approximate height of disappearing views. Could be bigger, than accurate value. */
    private int calcDisappearingViewsLength(RecyclerView.Recycler recycler) {
        int removedLength = 0;

        Integer minStart = Integer.MAX_VALUE;
        Integer maxEnd = Integer.MIN_VALUE;

        for (View view : childViews) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();

            boolean probablyMovedFromScreen = false;

            if (!lp.isItemRemoved()) {
                //view won't be removed, but maybe it is moved offscreen
                int pos = lp.getViewLayoutPosition();

                pos = recycler.convertPreLayoutPositionToPostLayout(pos);
                probablyMovedFromScreen = pos < minPositionOnScreen || pos > maxPositionOnScreen;
            }

            if (lp.isItemRemoved() || probablyMovedFromScreen) {
                deletingItemsOnScreenCount++;

                minStart = Math.min(minStart, stateFactory.getStart(view));
                maxEnd = Math.max(maxEnd, stateFactory.getEnd(view));
            }
        }

        if (minStart != Integer.MAX_VALUE) {
            removedLength = maxEnd - minStart;
        }

        return removedLength;
    }

    /**
     * place all added views to cache (in case scrolling)...
     */
    private void fillCache() {
        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            viewCache.put(pos, view);
        }
    }

    /**
     * find highest & lowest views
     */
    private void findBorderViews() {
        topView = null;
        bottomView = null;
        leftView = null;
        rightView = null;
        minPositionOnScreen = RecyclerView.NO_POSITION;
        maxPositionOnScreen = RecyclerView.NO_POSITION;

        isFirstItemAdded = false;

        if (getChildCount() > 0) {
            for (View view : childViews) {
                int position = getPosition(view);

                if (topView == null || getDecoratedTop(view) < getDecoratedTop(topView)) {
                    topView = view;
                }

                if (bottomView == null || getDecoratedBottom(view) > getDecoratedBottom(bottomView)) {
                    bottomView = view;
                }

                if (leftView == null || getDecoratedLeft(view) < getDecoratedLeft(leftView)) {
                    leftView = view;
                }

                if (rightView == null || getDecoratedRight(view) > getDecoratedRight(rightView)) {
                    rightView = view;
                }

                if (canvas.isInside(view) && (minPositionOnScreen == RecyclerView.NO_POSITION || position < minPositionOnScreen)) {
                    minPositionOnScreen = position;
                }

                if (canvas.isInside(view) && (maxPositionOnScreen == RecyclerView.NO_POSITION || position > maxPositionOnScreen)) {
                    maxPositionOnScreen = position;
                }

                if (position == 0) {
                    isFirstItemAdded = true;
                }
            }
        }
    }

    /**
     * place all views on theirs right places according to current state
     */
    private void fill(RecyclerView.Recycler recycler, LayouterFactory layouterFactory, @NonNull AnchorViewState anchorView, boolean isLayoutDisappearing) {
        int startingPos = anchorView.getPosition();
        Rect anchorRect = anchorView.getAnchorViewRect();

        fillCache();

        //... and remove from layout
        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }

        logger.onBeforeLayouter(anchorView);

        //up layouter should be invoked earlier than down layouter, because views with lower positions positioned above anchorView
        ILayouter upLayouter = layouterFactory.getBackwardLayouter(anchorRect);

        AbstractPositionIterator iterator = upLayouter.positionIterator();
        //start from anchor position
        iterator.move(startingPos - 1);
        logger.onStartLayouter(startingPos - 1);

        fillWithLayouter(recycler, upLayouter);
        ILayouter downLayouter = layouterFactory.getForwardLayouter(anchorRect);

        iterator = downLayouter.positionIterator();
        //start from anchor position
        iterator.move(startingPos);
        logger.onStartLayouter(startingPos);

        fillWithLayouter(recycler, downLayouter);

        logger.onAfterLayouter();
        //move to trash everything, which haven't used in this layout cycle
        //that views gone from a screen or was removed outside from adapter
        for (int i = 0; i < viewCache.size(); i++) {
            removeAndRecycleView(viewCache.valueAt(i), recycler);
            logger.onRemovedAndRecycled(i);
        }

        viewCache.clear();
        logger.onAfterRemovingViews();

        findBorderViews();

        if (isLayoutDisappearing) {
            layoutDisappearingViews(recycler, upLayouter, downLayouter);
        }
    }

    /**
     * place views in layout started from chosen position with chosen layouter
     */
    private void fillWithLayouter(RecyclerView.Recycler recycler, ILayouter layouter) {
        AbstractPositionIterator iterator = layouter.positionIterator();
        while (iterator.hasNext()) {
            int pos = iterator.next();
            View view = viewCache.get(pos);
            if (view == null) { // we don't have view from previous layouter stage, request new one
                try {
                    view = recycler.getViewForPosition(pos);
                } catch (IndexOutOfBoundsException e) {
                    /** WTF sometimes on prediction animation playing in case very fast sequential changes in adapter
                     * {@link #getItemCount} could return value bigger than real count of items
                     * & {@link RecyclerView.Recycler#getViewForPosition(int)} throws exception in this case!
                     * to handle it, just leave the loop*/
                    break;
                }

                logger.onItemRequested();

                if (!layouter.placeView(view)) {
                     /* reached end of visible bounds, exit.
                    recycle view, which was requested previously
                     */
                    recycler.recycleView(view);
                    logger.onItemRecycled();

                    break;
                }

            } else { //we have detached views from previous layouter stage, attach it if needed
                if (!layouter.onAttachView(view)) {
                    break;
                }

                //remove reattached view from cache
                viewCache.remove(pos);
            }

        }

        logger.onFinishedLayouter();

        //layout last row, in case iterator fully processed
        layouter.layoutRow();
    }

    /**
     * recycler should contain all recycled views from a longest row, not just 2 holders by default
     */
    private void calcRecyclerCacheSize(RecyclerView.Recycler recycler) {
        int viewsInRow = maxViewsInRow == null ? INT_ROW_SIZE_APPROXIMATELY_FOR_CACHE : maxViewsInRow;
        recycler.setViewCacheSize((int) (viewsInRow * FAST_SCROLLING_COEFFICIENT));
    }

    @Override
    public boolean canScrollHorizontally() {
        if (layoutOrientation() == HORIZONTAL) return false;

        findBorderViews();
        if (getChildCount() > 0) {
            int left = getDecoratedLeft(leftView);
            int right = getDecoratedRight(rightView);

            if (minPositionOnScreen == 0
                    && maxPositionOnScreen == getItemCount() - 1
                    && left >= getPaddingLeft()
                    && right <= getWidth() - getPaddingRight()) {
                return false;
            }
        } else {
            return false;
        }

        return isScrollingEnabledContract;
    }

    @RestrictTo(RestrictTo.Scope.GROUP_ID)
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        dx = scrollHorizontallyInternal(dx);
        offsetChildrenHorizontal(-dx);

        scrollingLogger.logChildCount(getChildCount());

        anchorView = anchorFactory.getAnchor();
        scrollingLogger.logAnchorView(anchorView);

        AbstractCriteriaFactory criteriaFactory = stateFactory.createDefaultFinishingCriteriaFactory();
        criteriaFactory.setAdditionalRowsCount(1);
        LayouterFactory factory = stateFactory.createLayouterFactory(criteriaFactory, placerFactory.createRealPlacerFactory());

        fill(recycler, factory, anchorView, false);
        return dx;
    }

    private int scrollHorizontallyInternal(int dx) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return 0;
        }

        int delta = 0;
        if (dx < 0) { //if content scrolled backward
            delta = onContentScrolledLeft(dx);
        } else if (dx > 0) { //if content scrolled forward
            delta = onContentScrolledRight(dx);
        }

        performNormalizationIfNeeded();

        return delta;
    }

    /** calculate dx, stop scrolling whether items bounds reached*/
    private int onContentScrolledRight(int dx) {
        int childCount = getChildCount();
        int itemCount = getItemCount();
        int delta;

        View lastView = getChildAt(childCount - 1);
        int lastViewAdapterPos = getPosition(lastView);
        if (lastViewAdapterPos < itemCount - 1) { //in case lower view isn't the last view in adapter
            delta = dx;
        } else { //in case lower view is the last view in adapter and wouldn't be any other view below
            int viewRight = getDecoratedRight(rightView);
            int parentRight = getWidth() - getPaddingRight();
            int distance = viewRight - parentRight;
            delta = Math.min(distance, dx);
        }

        return delta;
    }

    private int onContentScrolledLeft(int dx) {
        int delta;
        AnchorViewState state = anchorFactory.getAnchor();
        if (state.getAnchorViewRect() == null) {
            state.setAnchorViewRect(getCanvas().getCanvasRect());
        }

        if (!isFirstItemAdded) { //in case 0 position haven't added in layout yet
            delta = dx;
        } else {

            int leftBorder = getPaddingLeft();
            int viewLeft = state.getAnchorViewRect().left;
            int distance;
            distance = viewLeft - leftBorder;

            scrollingLogger.logUpScrollingNormalizationDistance(distance);

            if (distance >= 0) {
                // in case over scroll on top border
                delta = distance;
            } else {
                //in case first child showed partially
                delta = Math.max(distance, dx);
            }
        }
        return delta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canScrollVertically() {
        if (layoutOrientation() == VERTICAL) return false;

        findBorderViews();
        if (getChildCount() > 0) {
            int top = getDecoratedTop(topView);
            int bottom = getDecoratedBottom(bottomView);

            if (minPositionOnScreen == 0
                    && maxPositionOnScreen == getItemCount() - 1
                    && top >= getPaddingTop()
                    && bottom <= getHeight() - getPaddingBottom()) {
                return false;
            }
        } else {
            return false;
        }

        return isScrollingEnabledContract;
    }

    /**
     * calculate offset of views while scrolling, layout items on new places
     */
    @RestrictTo(RestrictTo.Scope.GROUP_ID)
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

        dy = scrollVerticallyInternal(dy);
        offsetChildrenVertical(-dy);
        anchorView = anchorFactory.getAnchor();

        scrollingLogger.logChildCount(getChildCount());

        //some bugs connected with displaying views from the last row, which not fully showed, so just add additional row to avoid a lot of it.
        AbstractCriteriaFactory criteriaFactory = stateFactory.createDefaultFinishingCriteriaFactory();
        criteriaFactory.setAdditionalRowsCount(1);

        LayouterFactory factory = stateFactory.createLayouterFactory(criteriaFactory, placerFactory.createRealPlacerFactory());

        fill(recycler, factory, anchorView, false);
        return dy;
    }

    private int scrollVerticallyInternal(int dy) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return 0;
        }

        int delta = 0;
        if (dy < 0) {   //if content scrolled down
            delta = onContentScrolledDown(dy);
        } else if (dy > 0) { //if content scrolled up
            delta = onContentScrolledUp(dy);
        }

        performNormalizationIfNeeded();

        return delta;
    }

    /**
     * invoked when content scrolled down (return to older items)
     *
     * @param dy not processed changing of y axis
     * @return delta. Calculated changing of y axis
     */
    private int onContentScrolledDown(int dy) {
        int delta;

        AnchorViewState state = anchorFactory.getAnchor();
        if (state.getAnchorViewRect() == null) {
            state.setAnchorViewRect(getCanvas().getCanvasRect());
        }

        if (state.getPosition() != 0) { //in case 0 position haven't added in layout yet
            delta = dy;
        } else { //in case top view is a first view in adapter and wouldn't be any other view above
            int topBorder = getPaddingTop();
            int viewTop = state.getAnchorViewRect().top;
            int distance;
            distance = viewTop - topBorder;

            scrollingLogger.logUpScrollingNormalizationDistance(distance);

            if (viewTop - topBorder >= 0) {
                // in case over scroll on top border
                delta = distance;
            } else {
                //in case first child showed partially
                distance = viewTop - topBorder;
                delta = Math.max(distance, dy);
            }
        }

        return delta;
    }

    /**
     * invoked when content scrolled up (to newer items)
     *
     * @param dy not processed changing of y axis
     * @return delta. Calculated changing of y axis
     */
    private int onContentScrolledUp(int dy) {
        int childCount = getChildCount();
        int itemCount = getItemCount();
        int delta;

        View lastView = getChildAt(childCount - 1);
        int lastViewAdapterPos = getPosition(lastView);
        if (lastViewAdapterPos < itemCount - 1) { //in case lower view isn't the last view in adapter
            delta = dy;
        } else { //in case lower view is the last view in adapter and wouldn't be any other view below
            int viewBottom = getDecoratedBottom(bottomView);
            int parentBottom = getHeight() - getPaddingBottom();
            delta = Math.min(viewBottom - parentBottom, dy);
        }

        return delta;
    }

    /**
     * after several layout changes our item views probably haven't placed on right places,
     * because we don't memorize whole positions of items.
     * So them should be normalized to real positions when we can do it.
     */
    private void performNormalizationIfNeeded() {
        if (cacheNormalizationPosition != null && getChildCount() > 0) {
            final View firstView = getChildAt(0);
            int firstViewPosition = getPosition(firstView);

            if (firstViewPosition < cacheNormalizationPosition ||
                    (cacheNormalizationPosition == 0 && cacheNormalizationPosition == firstViewPosition)) {
                //perform normalization when we have reached previous position then normalization position
                Log.d("normalization", "position = " + cacheNormalizationPosition + " top view position = " + firstViewPosition);
                Log.d(TAG, "cache purged from position " + firstViewPosition);
                viewPositionsStorage.purgeCacheFromPosition(firstViewPosition);
                //reset normalization position
                cacheNormalizationPosition = null;
                requestLayoutWithAnimations();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void scrollToPosition(int position) {
        if (position >= getItemCount() || position < 0) {
            Log.e("span layout manager", "Cannot scroll to " + position + ", item count " + getItemCount());
            return;
        }

        Integer lastCachePosition = viewPositionsStorage.getLastCachePosition();

        cacheNormalizationPosition = cacheNormalizationPosition != null ? cacheNormalizationPosition : lastCachePosition;

        if (lastCachePosition != null && position < lastCachePosition) {
            position = viewPositionsStorage.getStartOfRow(position);
        }

        anchorView = anchorFactory.createNotFound();
        anchorView.setPosition(position);

        //Trigger a new view layout
        super.requestLayout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
        if (position >= getItemCount() || position < 0) {
            Log.e("span layout manager", "Cannot scroll to " + position + ", item count " + getItemCount());
            return;
        }

        RecyclerView.SmoothScroller scroller = scrollingController.createSmoothScroller(recyclerView.getContext(), position, 150, anchorView);
        scroller.setTargetPosition(position);
        startSmoothScroll(scroller);
    }

    @Override
    public void setMeasuredDimension(int widthSize, int heightSize) {
        measureSupporter.measure(widthSize, heightSize);
        super.setMeasuredDimension(measureSupporter.getMeasuredWidth(), measureSupporter.getMeasuredHeight());
    }

    ///////////////////////////////////////////////////////////////////////////
    // data set changed events
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter,
                                 RecyclerView.Adapter newAdapter) {
        newAdapter.registerAdapterDataObserver((RecyclerView.AdapterDataObserver) measureSupporter);
        //Completely scrap the existing layout
        removeAllViews();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemsRemoved(final RecyclerView recyclerView, int positionStart, int itemCount) {
        adapterActionsLogger.onItemsRemoved(positionStart, itemCount);
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        onLayoutUpdatedFromPosition(positionStart);

        measureSupporter.onItemsRemoved(recyclerView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        adapterActionsLogger.onItemsAdded(positionStart, itemCount);
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        onLayoutUpdatedFromPosition(positionStart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        adapterActionsLogger.onItemsChanged();
        super.onItemsChanged(recyclerView);
        viewPositionsStorage.purge();
        onLayoutUpdatedFromPosition(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        adapterActionsLogger.onItemsUpdated(positionStart, itemCount);
        super.onItemsUpdated(recyclerView, positionStart, itemCount);
        onLayoutUpdatedFromPosition(positionStart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
        onItemsUpdated(recyclerView, positionStart, itemCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        adapterActionsLogger.onItemsMoved(from, to, itemCount);
        super.onItemsMoved(recyclerView, from, to, itemCount);
        onLayoutUpdatedFromPosition(Math.min(from, to));
    }

    private void onLayoutUpdatedFromPosition(int position) {
        Log.d(TAG, "cache purged from position " + position);
        viewPositionsStorage.purgeCacheFromPosition(position);
        int startRowPos = viewPositionsStorage.getStartOfRow(position);
        cacheNormalizationPosition = cacheNormalizationPosition == null ?
                startRowPos : Math.min(cacheNormalizationPosition, startRowPos);
    }

}
