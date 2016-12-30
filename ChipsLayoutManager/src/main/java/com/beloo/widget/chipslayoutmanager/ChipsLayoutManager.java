package com.beloo.widget.chipslayoutmanager;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;
import com.beloo.widget.chipslayoutmanager.anchor.IAnchorFactory;
import com.beloo.widget.chipslayoutmanager.layouter.ColumnsStateFactory;
import com.beloo.widget.chipslayoutmanager.layouter.ICanvas;
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
import com.beloo.widget.chipslayoutmanager.logger.LoggerFactory;
import com.beloo.widget.chipslayoutmanager.util.AssertionUtils;
import com.beloo.widget.chipslayoutmanager.util.LayoutManagerUtil;
import com.beloo.widget.chipslayoutmanager.util.testing.EmptySpy;
import com.beloo.widget.chipslayoutmanager.util.testing.ISpy;

public class ChipsLayoutManager extends RecyclerView.LayoutManager implements IChipsLayoutManagerContract,
        IStateHolder,
        ScrollingController.IScrollerListener {
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
    private ICanvas canvas;

    private IDisappearingViewsManager disappearingViewsManager;

    /** iterable over views added to RecyclerView */
    private ChildViewsIterable childViews = new ChildViewsIterable(this);

    private SparseArray<View> childViewPositions = new SparseArray<>();

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
    /** layoutOrientation of layout. Could have HORIZONTAL or VERTICAL style */
    @Orientation
    private int layoutOrientation = HORIZONTAL;
    @RowStrategy
    private int rowStrategy = STRATEGY_DEFAULT;
    private boolean isStrategyAppliedWithLastRow;
    /** @see #setSmoothScrollbarEnabled(boolean). True by default */
    private boolean isSmoothScrollbarEnabled = false;

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

    /** factory for placers factories*/
    private PlacerFactory placerFactory = new PlacerFactory(this);

    /** used for testing purposes to spy for {@link ChipsLayoutManager} behaviour */
    private ISpy spy = new EmptySpy();

    private boolean isAfterPreLayout;

    @SuppressWarnings("WeakerAccess")
    @VisibleForTesting
    ChipsLayoutManager(Context context) {
        @DeviceOrientation
        int orientation = context.getResources().getConfiguration().orientation;
        this.orientation = orientation;

        LoggerFactory loggerFactory = new LoggerFactory();
        logger = loggerFactory.getFillLogger(viewCache);
        adapterActionsLogger = loggerFactory.getAdapterActionsLogger();
        predictiveAnimationsLogger = loggerFactory.getPredictiveAnimationsLogger();

        viewPositionsStorage = new ViewCacheFactory(this).createCacheStorage();
        measureSupporter = new MeasureSupporter(this);
        setAutoMeasureEnabled(true);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ChipsLayoutManager contract methods
    ///////////////////////////////////////////////////////////////////////////

    public static Builder newBuilder(Context context) {
        if (context == null) throw new IllegalArgumentException("you have passed null context to builder");
        return new ChipsLayoutManager(context).new StrategyBuilder();
    }

    public IChildGravityResolver getChildGravityResolver() {
        return childGravityResolver;
    }

    /** use it to strictly disable scrolling.
     * If scrolling enabled it would be disabled in case all items fit on the screen */
    @Override
    public void setScrollingEnabledContract(boolean isEnabled) {
        isScrollingEnabledContract = isEnabled;
    }

    @Override
    public boolean isScrollingEnabledContract() {
        return isScrollingEnabledContract;
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

    @Override
    public Integer getMaxViewsInRow() {
        return maxViewsInRow;
    }

    @Override
    public IRowBreaker getRowBreaker() {
        return rowBreaker;
    }

    @Override
    @RowStrategy
    public int getRowStrategyType() {
        return rowStrategy;
    }

    ///////////////////////////////////////////////////////////////////////////
    // non-contract public methods. Used only for inner purposes
    ///////////////////////////////////////////////////////////////////////////

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public boolean isStrategyAppliedWithLastRow() {
        return isStrategyAppliedWithLastRow;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public IViewCacheStorage getViewPositionsStorage() {
        return viewPositionsStorage;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public ICanvas getCanvas() {
        return canvas;
    }

    @NonNull
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    AnchorViewState getAnchor() {
        return anchorView;
    }

    @VisibleForTesting
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    void setSpy(ISpy spy) {
        this.spy = spy;
    }

    ///////////////////////////////////////////////////////////////////////////
    // builder
    ///////////////////////////////////////////////////////////////////////////

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
            canvas = stateFactory.createCanvas();
            anchorFactory = stateFactory.anchorFactory();
            scrollingController = stateFactory.scrollingController();

            anchorView = anchorFactory.createNotFound();

            disappearingViewsManager = new DisappearingViewsManager(canvas, childViews, stateFactory);

            return ChipsLayoutManager.this;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    private void requestLayoutWithAnimations() {
        LayoutManagerUtil.requestLayoutWithAnimations(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // instance state
    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        container = (ParcelableContainer) state;

        anchorView = container.getAnchorViewState();
        if (orientation != container.getOrientation()) {
            //orientation have been changed, clear anchor rect
            int anchorPos = anchorView.getPosition();
            anchorView = anchorFactory.createNotFound();
            anchorView.setPosition(anchorPos);
        }

        viewPositionsStorage.onRestoreInstanceState(container.getPositionsCache(orientation));
        cacheNormalizationPosition = container.getNormalizationPosition(orientation);

        Log.d(TAG, "RESTORE. last cache position before cleanup = " + viewPositionsStorage.getLastCachePosition());
        if (cacheNormalizationPosition != null) {
            viewPositionsStorage.purgeCacheFromPosition(cacheNormalizationPosition);
        }
        viewPositionsStorage.purgeCacheFromPosition(anchorView.getPosition());
        Log.d(TAG, "RESTORE. anchor position =" + anchorView.getPosition());
        Log.d(TAG, "RESTORE. layoutOrientation = " + orientation + " normalizationPos = " + cacheNormalizationPosition);
        Log.d(TAG, "RESTORE. last cache position = " + viewPositionsStorage.getLastCachePosition());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parcelable onSaveInstanceState() {

        container.putAnchorViewState(anchorView);
        container.putPositionsCache(orientation, viewPositionsStorage.onSaveInstanceState());
        container.putOrientation(orientation);
        Log.d(TAG, "STORE. last cache position =" + viewPositionsStorage.getLastCachePosition());

        Integer storedNormalizationPosition = cacheNormalizationPosition != null ? cacheNormalizationPosition : viewPositionsStorage.getLastCachePosition();

        Log.d(TAG, "STORE. layoutOrientation = " + orientation + " normalizationPos = " + storedNormalizationPosition);

        container.putNormalizationPosition(orientation, storedNormalizationPosition);

        return container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // visible items
    ///////////////////////////////////////////////////////////////////////////

    /** returns count of completely visible views
     * @see #findFirstCompletelyVisibleItemPosition() ()
     * @see #findLastCompletelyVisibleItemPosition() */
    @SuppressWarnings("WeakerAccess")
    public int getCompletelyVisibleViewsCount() {
        int visibleViewsCount = 0;
        for (View child : childViews) {
            if (canvas.isFullyVisible(child)){
                visibleViewsCount++;
            }
        }

        return visibleViewsCount;
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
        return canvas.getMinPositionOnScreen();
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
        return canvas.getMaxPositionOnScreen();
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

    /** @return child for requested position. Null if that child haven't added to layout manager*/
    @Nullable
    View getChildWithPosition(int position) {
        return childViewPositions.get(position);
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

    ///////////////////////////////////////////////////////////////////////////
    // layouting
    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        //in pre-layouter drawing we need item count with items will be actually deleted to pre-draw appearing items properly
        return super.getItemCount() + disappearingViewsManager.getDeletingItemsOnScreenCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        spy.onLayoutChildren(recycler, state);
        Log.d(TAG, "onLayoutChildren. State =" + state);
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

        if (state.isPreLayout()) {
            //inside pre-layout stage. It is called when item animation reconstruction will be played
            //it is NOT called on layoutOrientation changes

            int additionalLength = disappearingViewsManager.calcDisappearingViewsLength(recycler);
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

            logger.onBeforeLayouter(anchorView);
            fill(recycler,
                    layouterFactory.getBackwardLayouter(anchorView),
                    layouterFactory.getForwardLayouter(anchorView));

            isAfterPreLayout = true;
        } else {
            detachAndScrapAttachedViews(recycler);

            //we perform layouting stage from scratch, so cache will be rebuilt soon, we could purge it and avoid unnecessary normalization
            viewPositionsStorage.purgeCacheFromPosition(anchorView.getPosition());
            if (cacheNormalizationPosition != null && anchorView.getPosition() <= cacheNormalizationPosition) {
                cacheNormalizationPosition = null;
            }

            /* In case some moving views
             * we should place it at layout to support predictive animations
             * we can't place all possible moves on theirs real place, because concrete layout position of particular view depends on placing of previous views
             * and there could be moving from 0 position to 10k. But it is preferably to place nearest moved view to real positions to make moving more natural
             * like moving from 0 position to 15 for example, where user could scroll fast and check
             * so we fill additional rows to cover nearest moves
             */
            AbstractCriteriaFactory criteriaFactory = stateFactory.createDefaultFinishingCriteriaFactory();
            criteriaFactory.setAdditionalRowsCount(APPROXIMATE_ADDITIONAL_ROWS_COUNT);

            LayouterFactory layouterFactory = stateFactory.createLayouterFactory(criteriaFactory, placerFactory.createRealPlacerFactory());
            ILayouter backwardLayouter = layouterFactory.getBackwardLayouter(anchorView);
            ILayouter forwardLayouter = layouterFactory.getForwardLayouter(anchorView);

            fill(recycler, backwardLayouter, forwardLayouter);

            /* should be executed before {@link #layoutDisappearingViews} */
            if (scrollingController.normalizeGaps(recycler, null)) {
                Log.d(TAG, "normalize gaps");
                //we should re-layout with new anchor after normalizing gaps
                anchorView = anchorFactory.getAnchor();
                requestLayoutWithAnimations();
            }

            if (isAfterPreLayout) {
                //we should layout disappearing views after pre-layout to support natural movements)
                layoutDisappearingViews(recycler, backwardLayouter, forwardLayouter);
            }

            isAfterPreLayout = false;
        }

        disappearingViewsManager.reset();

        if (!state.isMeasuring()) {
            measureSupporter.onSizeChanged();
        }

    }

    @Override
    public void detachAndScrapAttachedViews(RecyclerView.Recycler recycler) {
        super.detachAndScrapAttachedViews(recycler);
        childViewPositions.clear();
    }

    /** layout disappearing view to support predictive animations */
    private void layoutDisappearingViews(RecyclerView.Recycler recycler, @NonNull ILayouter upLayouter, ILayouter downLayouter) {

        ICriteriaFactory criteriaFactory = new InfiniteCriteriaFactory();
        LayouterFactory layouterFactory = stateFactory.createLayouterFactory(criteriaFactory, placerFactory.createDisappearingPlacerFactory());

        DisappearingViewsManager.DisappearingViewsContainer disappearingViews = disappearingViewsManager.getDisappearingViews(recycler);

        if (disappearingViews.size() > 0) {
            Log.d("disappearing views", "count = " + disappearingViews.size());
            Log.d("fill disappearing views", "");
            downLayouter = layouterFactory.buildForwardLayouter(downLayouter);

            //we should layout disappearing views left somewhere, just continue layout them in current layouter
            for (int i = 0; i< disappearingViews.getForwardViews().size(); i++) {
                int position = disappearingViews.getForwardViews().keyAt(i);
                downLayouter.placeView(recycler.getViewForPosition(position));
            }
            //layout last row
            downLayouter.layoutRow();

            upLayouter = layouterFactory.buildBackwardLayouter(upLayouter);
            //we should layout disappearing views left somewhere, just continue layout them in current layouter
            for (int i = 0; i< disappearingViews.getBackwardViews().size(); i++) {
                int position = disappearingViews.getBackwardViews().keyAt(i);
                upLayouter.placeView(recycler.getViewForPosition(position));
            }

            //layout last row
            upLayouter.layoutRow();
        }
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
     * place all views on theirs right places according to current state
     */
    private void fill(RecyclerView.Recycler recycler, ILayouter backwardLayouter, ILayouter forwardLayouter) {
        int startingPos = anchorView.getPosition();
        fillCache();

        //... and remove from layout
        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }

        logger.onStartLayouter(startingPos - 1);

        /* there is no sense to perform backward layouting when anchor is null.
           null anchor means that layout will be performed from absolutely top corner with start at anchor position
        */
        if (anchorView.getAnchorViewRect() != null) {
            //up layouter should be invoked earlier than down layouter, because views with lower positions positioned above anchorView
            //start from anchor position
            fillWithLayouter(recycler, backwardLayouter, startingPos - 1);
        }

        logger.onStartLayouter(startingPos);

        //start from anchor position
        fillWithLayouter(recycler, forwardLayouter, startingPos);

        logger.onAfterLayouter();
        //move to trash everything, which haven't used in this layout cycle
        //that views gone from a screen or was removed outside from adapter
        for (int i = 0; i < viewCache.size(); i++) {
            removeAndRecycleView(viewCache.valueAt(i), recycler);
            logger.onRemovedAndRecycled(i);
        }

        canvas.findBorderViews();
        buildChildWithPositionsMap();

        viewCache.clear();
        logger.onAfterRemovingViews();
    }

    private void buildChildWithPositionsMap() {
        childViewPositions.clear();
        for (View view : childViews) {
            int position = getPosition(view);
            childViewPositions.put(position, view);
        }
    }

    /**
     * place views in layout started from chosen position with chosen layouter
     */
    private void fillWithLayouter(RecyclerView.Recycler recycler, ILayouter layouter, int startingPos) {
        if (startingPos < 0) return;
        AbstractPositionIterator iterator = layouter.positionIterator();
        iterator.move(startingPos);
        while (iterator.hasNext()) {
            int pos = iterator.next();
            View view = viewCache.get(pos);
            if (view == null) { // we don't have view from previous layouter stage, request new one
                try {
                    view = recycler.getViewForPosition(pos);
                } catch (IndexOutOfBoundsException e) {
                    /* WTF sometimes on prediction animation playing in case very fast sequential changes in adapter
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

    ///////////////////////////////////////////////////////////////////////////
    // measure
    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMeasuredDimension(int widthSize, int heightSize) {
        measureSupporter.measure(widthSize, heightSize);
        Log.i(TAG, "measured dimension = " + heightSize);
        super.setMeasuredDimension(measureSupporter.getMeasuredWidth(), measureSupporter.getMeasuredHeight());
    }

    ///////////////////////////////////////////////////////////////////////////
    // data set changed events
    ///////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter,
                                 RecyclerView.Adapter newAdapter) {
        if (oldAdapter != null && measureSupporter.isRegistered()) {
            try {
                measureSupporter.setRegistered(false);
                oldAdapter.unregisterAdapterDataObserver((RecyclerView.AdapterDataObserver) measureSupporter);
            } catch (IllegalStateException e) {
                //skip unregister errors
            }
        }
        if (newAdapter != null) {
            measureSupporter.setRegistered(true);
            newAdapter.registerAdapterDataObserver((RecyclerView.AdapterDataObserver) measureSupporter);
        }
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

    /** update cache according to data changes */
    private void onLayoutUpdatedFromPosition(int position) {
        Log.d(TAG, "cache purged from position " + position);
        viewPositionsStorage.purgeCacheFromPosition(position);
        int startRowPos = viewPositionsStorage.getStartOfRow(position);
        cacheNormalizationPosition = cacheNormalizationPosition == null ?
                startRowPos : Math.min(cacheNormalizationPosition, startRowPos);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Scrolling
    ///////////////////////////////////////////////////////////////////////////

    /**
     * When smooth scrollbar is enabled, the position and size of the scrollbar thumb is computed
     * based on the number of visible pixels in the visible items. This however assumes that all
     * list items have similar or equal widths or heights (depending on list orientation).
     *
     * Also this is {@link ChipsLayoutManager} specific issue, that we can't predict exact count of items on screen
     * in general case, because we can't predict items count in row.
     * So to enable it you should accomplish one of those conditions:
     * <ul>
     *     <li> Your items have same width and height </li>
     *     <li> You have {@link ChipsLayoutManager#setMaxViewsInRow(Integer)} set and you able to make sure, that there won't be many rows with lower items count.
     *     The best is none. </li>
     * </ul>
     *
     * If you use a list in which items have different dimensions, the scrollbar will change
     * appearance as the user scrolls through the list. To avoid this issue,  you need to disable
     * this property.
     *
     * When smooth scrollbar is disabled, the position and size of the scrollbar thumb is based
     * solely on the number of items in the adapter and the position of the visible items inside
     * the adapter. This provides a stable scrollbar as the user navigates through a list of items
     * with varying widths / heights.
     *
     * @param enabled Whether or not to enable smooth scrollbar.
     *
     * @see #isSmoothScrollbarEnabled()
     */
    @Override
    public void setSmoothScrollbarEnabled(boolean enabled) {
        isSmoothScrollbarEnabled = enabled;
    }

    /**
     * Returns the current state of the smooth scrollbar feature. It is NOT enabled by default.
     *
     * @return True if smooth scrollbar is enabled, false otherwise.
     *
     * @see #setSmoothScrollbarEnabled(boolean)
     */
    @Override
    public boolean isSmoothScrollbarEnabled() {
        return isSmoothScrollbarEnabled;
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
    public boolean canScrollHorizontally() {
        return scrollingController.canScrollHorizontally();
    }

    @Override
    public boolean canScrollVertically() {
        return scrollingController.canScrollVertically();
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollingController.scrollVerticallyBy(dy, recycler, state);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollingController.scrollHorizontallyBy(dx, recycler, state);
    }

    public VerticalScrollingController verticalScrollingController() {
        return new VerticalScrollingController(this, stateFactory, this);
    }

    public HorizontalScrollingController horizontalScrollingController() {
        return new HorizontalScrollingController(this, stateFactory, this);
    }

    @Override
    public void onScrolled(IScrollingController scrollingController, RecyclerView.Recycler recycler, RecyclerView.State state) {

        performNormalizationIfNeeded();
        anchorView = anchorFactory.getAnchor();

        AbstractCriteriaFactory criteriaFactory = stateFactory.createDefaultFinishingCriteriaFactory();
        criteriaFactory.setAdditionalRowsCount(1);
        LayouterFactory factory = stateFactory.createLayouterFactory(criteriaFactory, placerFactory.createRealPlacerFactory());

        fill(recycler,
                factory.getBackwardLayouter(anchorView),
                factory.getForwardLayouter(anchorView));
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Override
    public int computeVerticalScrollOffset(RecyclerView.State state) {
        return scrollingController.computeVerticalScrollOffset(state);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Override
    public int computeVerticalScrollExtent(RecyclerView.State state) {
        return scrollingController.computeVerticalScrollExtent(state);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Override
    public int computeVerticalScrollRange(RecyclerView.State state) {
        return scrollingController.computeVerticalScrollRange(state);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Override
    public int computeHorizontalScrollExtent(RecyclerView.State state) {
        return scrollingController.computeHorizontalScrollExtent(state);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Override
    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        return scrollingController.computeHorizontalScrollOffset(state);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Override
    public int computeHorizontalScrollRange(RecyclerView.State state) {
        return scrollingController.computeHorizontalScrollRange(state);
    }
}
