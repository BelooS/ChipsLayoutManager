package com.beloo.widget.chipslayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorFactory;
import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;
import com.beloo.widget.chipslayoutmanager.anchor.IAnchorFactory;
import com.beloo.widget.chipslayoutmanager.breaker.EmptyRowBreaker;
import com.beloo.widget.chipslayoutmanager.breaker.IRowBreaker;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.cache.ViewCacheFactory;
import com.beloo.widget.chipslayoutmanager.gravity.CenterChildGravity;
import com.beloo.widget.chipslayoutmanager.gravity.CustomGravityResolver;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouterFactory;
import com.beloo.widget.chipslayoutmanager.layouter.AbstractPositionIterator;
import com.beloo.widget.chipslayoutmanager.layouter.ILayouter;
import com.beloo.widget.chipslayoutmanager.layouter.Item;
import com.beloo.widget.chipslayoutmanager.layouter.LTRLayouterFactory;
import com.beloo.widget.chipslayoutmanager.layouter.RTLLayouterFactory;
import com.beloo.widget.chipslayoutmanager.logger.IAdapterActionsLogger;
import com.beloo.widget.chipslayoutmanager.logger.IFillLogger;
import com.beloo.widget.chipslayoutmanager.logger.IPredictiveAnimationsLogger;
import com.beloo.widget.chipslayoutmanager.logger.LoggerFactory;
import com.beloo.widget.chipslayoutmanager.util.AssertionUtils;

import java.util.List;

import timber.log.Timber;

public class ChipsLayoutManager extends RecyclerView.LayoutManager implements IChipsLayoutManagerContract {
    private static final String TAG = ChipsLayoutManager.class.getSimpleName();
    private static final int INT_ROW_SIZE_APPROXIMATELY_FOR_CACHE = 10;
    private static final int APPROXIMATE_ADDITIONAL_ROWS_COUNT = 5;
    /**
     * coefficient to support fast scrolling, caching views only for one row may not be enough
     */
    private static final float FAST_SCROLLING_COEFFICIENT = 2;

    /** iterable over views added to RecyclerView */
    private ChildViewsIterable childViews = new ChildViewsIterable(this);

    //---- contract parameters
    /** determine gravity of child inside row*/
    private IChildGravityResolver childGravityResolver;
    private boolean isScrollingEnabledContract = true;
    /** strict restriction of max count of views in particular row */
    private Integer maxViewsInRow = null;
    /** determines whether LM should break row from view position */
    private IRowBreaker rowBreaker = new EmptyRowBreaker();
    //--- end contract parameters

    /** store positions of placed view to know when LM should break row while moving back */
    private IViewCacheStorage viewPositionsStorage;

    /**
     * store detached views to probably reattach it if them still visible.
     * Used while scrolling
     */
    private SparseArray<View> viewCache = new SparseArray<>();

    /**
     * storing state due orientation changes
     */
    private ParcelableContainer container = new ParcelableContainer();

    //---loggers below
    private IFillLogger logger;
    private IAdapterActionsLogger adapterActionsLogger;
    private IPredictiveAnimationsLogger predictiveAnimationsLogger;
    //--- end loggers


    /**
     * is layout in RTL mode. Variable needed to detect mode changes
     */
    private boolean isLayoutRTL = false;

    /**
     * highest view in layout. Have always actual value, because it set in {@link #onLayoutChildren}
     */
    private View highestView;
    /**
     * lowest view in layout. Have always actual value, because it set in {@link #onLayoutChildren}
     */
    private View lowestView;

    /**
     * current device orientation
     */
    @DeviceOrientation
    private int orientation;

    /**
     * when scrolling reached this position {@link ChipsLayoutManager} is able to restore items layout according to cached items with positions above.
     * That layout would exactly correspond to current item view situation
     */
    @Nullable
    private Integer cacheNormalizationPosition = null;

    /**
     * height of RecyclerView before removing item
     */
    private Integer beforeRemovingHeight = null;

    /**
     * height which we receive after {@link #onLayoutChildren} method finished.
     * Contains correct height after auto-measuring
     */
    private int autoMeasureHeight = 0;

    /** factory which could retrieve anchorView on which layouting based*/
    private IAnchorFactory anchorFactory = new AnchorFactory(this);

    /**
     * stored current anchor view due to scroll state changes
     */
    private AnchorViewState anchorView = anchorFactory.createNotFound();

    /* in pre-layouter drawing we need item count with items will be actually deleted to pre-draw appearing items properly
    * buf value*/
    private int deletingItemsOnScreenCount;

    private ChipsLayoutManager(Context context) {
        //Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        @DeviceOrientation
        int orientation = context.getResources().getConfiguration().orientation;
        this.orientation = orientation;

        LoggerFactory loggerFactory = new LoggerFactory();
        logger = loggerFactory.getFillLogger();
        adapterActionsLogger = loggerFactory.getAdapterActionsLogger();
        predictiveAnimationsLogger = loggerFactory.getPredictiveAnimationsLogger();

        viewPositionsStorage = new ViewCacheFactory(this).createCacheStorage();
        setAutoMeasureEnabled(true);
    }

    private AbstractLayouterFactory createLayouterFactory() {
        AbstractLayouterFactory layouterFactory = isLayoutRTL() ?
                new RTLLayouterFactory(this, viewPositionsStorage, rowBreaker) : new LTRLayouterFactory(this, viewPositionsStorage, rowBreaker);
        layouterFactory.setMaxViewsInRow(maxViewsInRow);
        return layouterFactory;
    }

    public static Builder newBuilder(Context context) {
        return new ChipsLayoutManager(context).new Builder();
    }

    public IChildGravityResolver getChildGravityResolver() {
        return childGravityResolver;
    }

    /** use it to strcitly disable scrolling.
     * If scrolling enabled it would be disabled in case all items fit on the screen */
    public void setScrollingEnabledContract(boolean isEnabled) {
        isScrollingEnabledContract = isEnabled;
    }

    /**
     * change max count of row views in runtime
     */
    public void setMaxViewsInRow(@IntRange(from = 1) Integer maxViewsInRow) {
        if (maxViewsInRow < 1)
            throw new IllegalArgumentException("maxViewsInRow should be positive, but is = " + maxViewsInRow);
        this.maxViewsInRow = maxViewsInRow;
        cacheNormalizationPosition = 0;
        viewPositionsStorage.purge();
        requestLayoutWithAnimations();
    }

    public class Builder {

        @SpanLayoutChildGravity
        private Integer gravity;

        private Builder() {
        }

        /**
         * set vertical gravity in a row for all children. Default = CENTER_VERTICAL
         */
        public Builder setChildGravity(@SpanLayoutChildGravity int gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * set gravity resolver in case you need special gravity for items. This method have priority over {@link #setChildGravity(int)}
         */
        public Builder setGravityResolver(@NonNull IChildGravityResolver gravityResolver) {
            AssertionUtils.assertNotNull(gravityResolver, "gravity resolver couldn't be null");
            childGravityResolver = gravityResolver;
            return this;
        }

        /**
         * strictly disable scrolling if needed
         */
        public Builder setScrollingEnabled(boolean isEnabled) {
            ChipsLayoutManager.this.setScrollingEnabledContract(isEnabled);
            return this;
        }

        /**
         * set maximum possible count of views in row
         */
        public Builder setMaxViewsInRow(@IntRange(from = 1) int maxViewsInRow) {
            if (maxViewsInRow < 1)
                throw new IllegalArgumentException("maxViewsInRow should be positive, but is = " + maxViewsInRow);
            ChipsLayoutManager.this.maxViewsInRow = maxViewsInRow;
            return this;
        }

        /** @param breaker override to determine whether ChipsLayoutManager should breaks row due to position of view */
        public Builder setRowBreaker(@NonNull IRowBreaker breaker) {
            AssertionUtils.assertNotNull(breaker, "breaker couldn't be null");
            ChipsLayoutManager.this.rowBreaker = breaker;
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
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter,
                                 RecyclerView.Adapter newAdapter) {
        //Completely scrap the existing layout
        newAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                // on api 16 this is invoked before onItemsRemoved
                super.onItemRangeRemoved(positionStart, itemCount);
                beforeRemovingHeight = autoMeasureHeight;
                /** we detected removing event, so should process measuring manually
                 * @see <a href="http://stackoverflow.com/questions/40242011/custom-recyclerviews-layoutmanager-automeasuring-after-animation-finished-on-i">Stack Overflow issue</a>
                 */
                setAutoMeasureEnabled(false);
            }
        });

        removeAllViews();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        container = (ParcelableContainer) state;
        anchorView = anchorFactory.createNotFound();
        anchorView.setPosition(container.getAnchorPosition());

        viewPositionsStorage.onRestoreInstanceState(container.getPositionsCache(orientation));
        cacheNormalizationPosition = 0;
//        cacheNormalizationPosition = container.getNormalizationPosition(orientation);
        Timber.d("RESTORE. orientation = " + orientation + " normalizationPos = " + cacheNormalizationPosition);
    }

    @Override
    public Parcelable onSaveInstanceState() {

        //store only position on anchor. Rect of anchor will be invalidated
        int anchorPosition = anchorView.getPosition();
        container.putAnchorPosition(anchorPosition);

        //todo not worked now. will be provided in next releases
//        container.putPositionsCache(orientation, viewPositionsStorage.onSaveInstanceState());
//
//        Integer storedNormalizationPosition;
//        if (!viewPositionsStorage.isCacheEmpty()) {
//            storedNormalizationPosition = cacheNormalizationPosition != null ? cacheNormalizationPosition : viewPositionsStorage.getLastCachePosition();
//        } else {
//            storedNormalizationPosition = cacheNormalizationPosition;
//        }
//        Timber.d(TAG, "STORE. orientation = " + orientation + " normalizationPos = " + storedNormalizationPosition);
//
//        container.putNormalizationPosition(orientation, storedNormalizatdionPosition);

        return container;
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
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
            viewPositionsStorage.purge();
            //so detach all views before we start searching for anchor view
            detachAndScrapAttachedViews(recycler);

        }

        calcRecyclerCacheSize(recycler);

        if (!state.isPreLayout()) {
            detachAndScrapAttachedViews(recycler);

            AbstractLayouterFactory layouterFactory = createLayouterFactory();
            /** In case some moving views
             * we should place it at layout to support predictive animations
             * we can't place all possible moves on theirs real place, because concrete layout position of particular view depends on placing of previous views
             * and there could be moving from 0 position to 10k. But it is preferably to place nearest moved view to real positions to make moving more natural
             * like moving from 0 position to 15 for example, where user could scroll fast and check
             */
            //so we fill additional rows to cover nearest moves
            layouterFactory.setAdditionalRowsCount(APPROXIMATE_ADDITIONAL_ROWS_COUNT);
            fill(recycler, layouterFactory, anchorView, true);

            performNormalizationIfNeeded();
        } else {
            int additionalHeight = calcDisappearingViewsHeight(recycler);
            predictiveAnimationsLogger.heightOfCanvas(this);
            predictiveAnimationsLogger.onSummarizedDeletingItemsHeightCalculated(additionalHeight);
            anchorView = anchorFactory.getTopLeftAnchor();
            detachAndScrapAttachedViews(recycler);

            //in case removing draw additional rows to show predictive animations for appearing views
            AbstractLayouterFactory layouterFactory = createLayouterFactory();
            layouterFactory.setAdditionalHeight(additionalHeight);
            layouterFactory.setAdditionalRowsCount(APPROXIMATE_ADDITIONAL_ROWS_COUNT);

            fill(recycler, layouterFactory, anchorView, false);
        }

        deletingItemsOnScreenCount = 0;

        autoMeasureHeight = getHeight();
    }

    /** layout disappearing view to support predictive animations */
    private void layoutDisappearingViews(RecyclerView.Recycler recycler, AbstractLayouterFactory layouterFactory,
                                         ILayouter upLayouter, ILayouter downLayouter) {
        DisappearingViewsContainer disappearingViews = getDisappearingViews(recycler);
        Timber.d("disappearing views count = " + disappearingViews.size());

        if (disappearingViews.size() > 0) {
            Timber.d("fill disappearing views");
            downLayouter = layouterFactory.buildInfiniteLayouter(layouterFactory.getDisappearingDownLayouter(downLayouter));

            //we should layout disappearing views left somewhere, just continue layout them in current layouter
            for (int i = 0; i< disappearingViews.downViews.size(); i++) {
                int position = disappearingViews.downViews.keyAt(i);
                downLayouter.placeView(recycler.getViewForPosition(position));
            }
            //layout last row
            downLayouter.layoutRow();

            upLayouter = layouterFactory.buildInfiniteLayouter(layouterFactory.getDisappearingUpLayouter(upLayouter));
            //we should layout disappearing views left somewhere, just continue layout them in current layouter
            for (int i = 0; i< disappearingViews.upViews.size(); i++) {
                int position = disappearingViews.upViews.keyAt(i);
                upLayouter.placeView(recycler.getViewForPosition(position));
            }

            //layout last row
            upLayouter.layoutRow();
        }
    }

    private class DisappearingViewsContainer {
        private SparseArray<View> upViews = new SparseArray<>();
        private SparseArray<View> downViews = new SparseArray<>();

        int size() {
            return upViews.size() + downViews.size();
        }

        void remove(int position) {
            upViews.remove(position);
            downViews.remove(position);
        }


    }

    public DisappearingViewsContainer getDisappearingViews(RecyclerView.Recycler recycler) {
        final List<RecyclerView.ViewHolder> scrapList = recycler.getScrapList();
        //views which moved from screen, but not deleted
        DisappearingViewsContainer container = new DisappearingViewsContainer();

        int highestViewPosition = getPosition(highestView);
        int lowestViewPosition = getPosition(lowestView);

        for (RecyclerView.ViewHolder holder : scrapList) {
            final View child = holder.itemView;
            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (!lp.isItemRemoved()) {
                if (lp.getViewAdapterPosition() < highestViewPosition) {
                    container.upViews.put(lp.getViewAdapterPosition(), child);
                } else if (lp.getViewAdapterPosition() > lowestViewPosition) {
                    container.downViews.put(lp.getViewAdapterPosition(), child);
                }
            }
        }

        return container;
    }
    /** during pre-layout calculate approximate height which will be free after moving items offscreen (removed or moved)
     * @return approximate height of disappearing views. Could be bigger, than accurate value. */
    private int calcDisappearingViewsHeight(RecyclerView.Recycler recycler) {
        int removedHeight = 0;

        Integer minRemovedTop = Integer.MAX_VALUE;
        Integer maxRemovedBottom = Integer.MIN_VALUE;

        for (View view : childViews) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();

            boolean probablyMovedFromScreen = false;

            if (!lp.isItemRemoved()) {
                //view won't be removed, but maybe it moved offscreen
                int pos = lp.getViewLayoutPosition();
                int lowestPosition = getPosition(highestView);
                int highestPosition = getPosition(lowestView);

                pos = recycler.convertPreLayoutPositionToPostLayout(pos);
                probablyMovedFromScreen = pos < lowestPosition || pos > highestPosition;
            }

            if (lp.isItemRemoved() || probablyMovedFromScreen) {
                deletingItemsOnScreenCount++;
                minRemovedTop = Math.min(minRemovedTop, getDecoratedTop(view));
                maxRemovedBottom = Math.max(maxRemovedBottom, getDecoratedBottom(view));
            }
        }

        if (minRemovedTop != Integer.MAX_VALUE) {
            removedHeight = maxRemovedBottom - minRemovedTop;
        }

        return removedHeight;
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
    private void findHighestAndLowestViews() {
        highestView = null;
        lowestView = null;

        if (getChildCount() > 0) {
            highestView = getChildAt(0);
            lowestView = highestView;
            for (int i = 1; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (getDecoratedTop(view) < getDecoratedTop(highestView)) {
                    highestView = view;
                }

                if (getDecoratedBottom(view) > getDecoratedBottom(lowestView)) {
                    lowestView = view;
                }
            }
        }
    }

    /**
     * place all views on theirs right places according to current state
     */
    private void fill (RecyclerView.Recycler recycler, @NonNull AnchorViewState anchorView) {
        fill(recycler, createLayouterFactory(), anchorView, false);
    }

    /**
     * place all views on theirs right places according to current state
     */
    private void fill(RecyclerView.Recycler recycler, AbstractLayouterFactory layouterFactory, @NonNull AnchorViewState anchorView, boolean isLayoutDisappearing) {
        int startingPos = anchorView.getPosition();
        Rect anchorRect = anchorView.getAnchorViewRect();

        fillCache();

        //... and remove from layout
        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }

        logger.onBeforeLayouter(anchorView);

        //up layouter should be invoked earlier than down layouter, because views with lower positions positioned above anchorView
        ILayouter upLayouter = layouterFactory.getUpLayouter(anchorRect);

        AbstractPositionIterator iterator = upLayouter.positionIterator();
        //start from anchor position
        iterator.move(startingPos - 1);
        logger.onStartLayouter(startingPos - 1);

        fillWithLayouter(recycler, upLayouter);
        ILayouter downLayouter = layouterFactory.getDownLayouter(anchorRect);

        iterator = downLayouter.positionIterator();
        //start from anchor position
        iterator.move(startingPos);
        logger.onStartLayouter(startingPos);

        fillWithLayouter(recycler, downLayouter);

        findHighestAndLowestViews();

        logger.onAfterLayouter();
        //move to trash everything, which haven't used in this layout cycle
        //that views gone from a screen or was removed outside from adapter
        for (int i = 0; i < viewCache.size(); i++) {
            removeAndRecycleView(viewCache.valueAt(i), recycler);
            logger.onRemovedAndRecycled(i);
        }

        viewCache.clear();
        logger.onAfterRemovingViews();

        if (isLayoutDisappearing) {
            layoutDisappearingViews(recycler, layouterFactory, upLayouter, downLayouter);
        }
    }

    /**
     * @return true if RTL mode enabled in RecyclerView
     */
    private boolean isLayoutRTL() {
        return getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canScrollVertically() {
        findHighestAndLowestViews();
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            View lastChild = getChildAt(getChildCount() - 1);

            int firstViewPosition = getPosition(view);
            int lastViewPosition = getPosition(lastChild);

            int top = getDecoratedTop(highestView);
            int bottom = getDecoratedBottom(lowestView);

            if (firstViewPosition == 0
                    && lastViewPosition == getItemCount() - 1
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
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        dy = scrollVerticallyInternal(dy);
        offsetChildrenVertical(-dy);
        anchorView = anchorFactory.getTopLeftAnchor();

        AbstractLayouterFactory factory = createLayouterFactory();
        //some bugs connected with displaying views from the last row, which not fully showed, so just add additional row to avoid a lot of it.
        factory.setAdditionalRowsCount(1);
        fill(recycler, factory, anchorView, false);
        return dy;
    }

    /**
     * perform changing layout with playing RecyclerView animations
     */
    private void requestLayoutWithAnimations() {
        postOnAnimation(() -> {
            requestLayout();
            requestSimpleAnimationsInNextLayout();
        });
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

        performNormalizationIfNeeded();

        AnchorViewState state = anchorFactory.getTopLeftAnchor();

        if (state.getPosition() != 0) { //in case 0 position haven't added in layout yet
            delta = dy;
        } else { //in case top view is a first view in adapter and wouldn't be any other view above
            int topBorder = getPaddingTop();
            int viewTop = state.getAnchorViewRect().top;
            int distance;
            distance = viewTop - topBorder;
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
            int viewBottom = getDecoratedBottom(lowestView);
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
        final View topView = getChildAt(0);
        if (topView == null) return;
        int topViewPosition = getPosition(topView);
        //perform normalization when we have reached previous position then normalization position
        if (cacheNormalizationPosition != null && (topViewPosition < cacheNormalizationPosition ||
                (cacheNormalizationPosition == 0 && cacheNormalizationPosition == topViewPosition))) {
            Timber.d("normalization, position = " + cacheNormalizationPosition + " top view position = " + topViewPosition);
            viewPositionsStorage.purgeCacheFromPosition(cacheNormalizationPosition);
            //reset normalization position
            cacheNormalizationPosition = null;
            requestLayoutWithAnimations();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void scrollToPosition(int position) {
        if (position >= getItemCount() || position < 0) {
            Timber.e("span layout manager", "Cannot scroll to " + position + ", item count " + getItemCount());
            return;
        }

        cacheNormalizationPosition = cacheNormalizationPosition != null ? cacheNormalizationPosition : viewPositionsStorage.getLastCachePosition();
        anchorView = anchorFactory.getTopLeftAnchor();
        anchorView.setPosition(position);

        //Trigger a new view layout
        requestLayout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
        if (position >= getItemCount() || position < 0) {
            Timber.e("span layout manager", "Cannot scroll to " + position + ", item count " + getItemCount());
            return;
        }

        LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext()) {
            /*
             * LinearSmoothScroller, at a minimum, just need to know the vector
             * (x/y distance) to travel in order to get from the current positioning
             * to the target.
             */
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                int visiblePosition = anchorView.getPosition();
                //determine scroll up or scroll down needed
                return new PointF(0, position > visiblePosition ? 1 : -1);
            }

            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                super.onTargetFound(targetView, state, action);
                int desiredTop = getPaddingTop();
                int currentTop = getDecoratedTop(targetView);

                int dy = currentTop - desiredTop;

                //perform fit animation to move target view at top of layout
                action.update(0, dy, 50, new LinearInterpolator());
            }
        };
        scroller.setTargetPosition(position);
        startSmoothScroll(scroller);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);

        if (!isAutoMeasureEnabled()) {
            // we should perform measuring manually
            // so request animations
            requestSimpleAnimationsInNextLayout();
            //keep height until remove animation will be completed
            setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), beforeRemovingHeight);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemsRemoved(final RecyclerView recyclerView, int positionStart, int itemCount) {
        adapterActionsLogger.onItemsRemoved(positionStart, itemCount);
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        onLayoutUpdatedFromPosition(positionStart);

        //subscribe to next animations tick
        postOnAnimation(() -> {
            //listen removing animation
            recyclerView.getItemAnimator().isRunning(() -> {
                //when removing animation finished return auto-measuring back
                setAutoMeasureEnabled(true);
                // and process onMeasure again
                requestLayout();
            });
        });
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
        viewPositionsStorage.purgeCacheFromPosition(position);
        int startRowPos = viewPositionsStorage.getStartOfRow(position);
        cacheNormalizationPosition = cacheNormalizationPosition == null ?
                startRowPos : Math.min(cacheNormalizationPosition, startRowPos);
    }

}
