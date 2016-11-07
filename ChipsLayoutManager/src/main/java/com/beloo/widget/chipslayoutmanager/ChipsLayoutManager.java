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
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.cache.ViewCacheFactory;
import com.beloo.widget.chipslayoutmanager.gravity.CenterChildGravity;
import com.beloo.widget.chipslayoutmanager.gravity.CustomGravityResolver;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouterFactory;
import com.beloo.widget.chipslayoutmanager.layouter.AbstractPositionIterator;
import com.beloo.widget.chipslayoutmanager.layouter.ILayouter;
import com.beloo.widget.chipslayoutmanager.layouter.ILayouterListener;
import com.beloo.widget.chipslayoutmanager.layouter.Item;
import com.beloo.widget.chipslayoutmanager.layouter.LTRLayouterFactory;
import com.beloo.widget.chipslayoutmanager.layouter.RTLLayouterFactory;
import com.beloo.widget.chipslayoutmanager.logger.IAdapterActionsLogger;
import com.beloo.widget.chipslayoutmanager.logger.IFillLogger;
import com.beloo.widget.chipslayoutmanager.logger.IPredictiveAnimationsLogger;
import com.beloo.widget.chipslayoutmanager.logger.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChipsLayoutManager extends RecyclerView.LayoutManager implements IChipsLayoutManagerContract {
    private static final String TAG = ChipsLayoutManager.class.getSimpleName();
    private static final int INT_ROW_SIZE_APPROXIMATELY_FOR_CACHE = 10;
    /**
     * coefficient to support fast scrolling, caching views only for one row may not be enough
     */
    private static final float FAST_SCROLLING_COEFFICIENT = 2;

    /** iterable over views added to RecyclerView */
    private ChildViewsIterable childViews = new ChildViewsIterable(this);

    //---- contract parameters
    private IChildGravityResolver childGravityResolver;
    private boolean isScrollingEnabledContract = true;
    private Integer maxViewsInRow = null;
    //--- end contract parameters

    private IViewCacheStorage viewPositionsStorage;

    /**
     * store detached views to probably reattach it if them still visible.
     * Used while scrolling
     */
    private SparseArray<View> viewCache = new SparseArray<>();

    /** map of views, which will be deleted after pre-layout */
    private SparseArray<View> removedViewCache = new SparseArray<>();

    HashMap<Rect, List<Item>> visibleRowsMap = new HashMap<>();

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

    private IAnchorFactory anchorFactory = new AnchorFactory(this);

    /**
     * stored current anchor view due to scroll state changes
     */
    private AnchorViewState anchorView = anchorFactory.createNotFound();

    private int deletingItemsOnScreenCount;

    private ChipsLayoutManager(Context context) {
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
                new RTLLayouterFactory(this, viewPositionsStorage) : new LTRLayouterFactory(this, viewPositionsStorage);
        layouterFactory.setMaxViewsInRow(maxViewsInRow);
        layouterFactory.setLayouterListener(layouter -> visibleRowsMap.put(layouter.getRowRect(), layouter.getCurrentRowItems()));
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
        public Builder setGravityResolver(IChildGravityResolver gravityResolver) {
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
        Log.d(TAG, "RESTORE. orientation = " + orientation + " normalizationPos = " + cacheNormalizationPosition);
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
//        Log.d(TAG, "STORE. orientation = " + orientation + " normalizationPos = " + storedNormalizationPosition);
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
            fill(recycler, layouterFactory, anchorView);

            layoutDisappearingViews(recycler, layouterFactory);
        } else {
            int additionalHeight = calcDisappearingViewsHeight(recycler);
            predictiveAnimationsLogger.heightOfCanvas(this);
            predictiveAnimationsLogger.onSummarizedDeletingItemsHeightCalculated(additionalHeight);
            anchorView = anchorFactory.getTopLeftAnchor();
            detachAndScrapAttachedViews(recycler);

            //in case removing draw additional rows to show predictive animations for appearing views
            AbstractLayouterFactory layouterFactory = createLayouterFactory();
            layouterFactory.setAdditionalHeight(additionalHeight);
            layouterFactory.setAdditionalRowsCount(5);

            fill(recycler, layouterFactory, anchorView);

            layoutDisappearingViews(recycler, layouterFactory);
        }

        deletingItemsOnScreenCount = 0;

        autoMeasureHeight = getHeight();
    }

    /** layout disappearing view to support predictive animations */
    private void layoutDisappearingViews(RecyclerView.Recycler recycler, AbstractLayouterFactory layouterFactory) {
        DisappearingViewsContainer disappearingViews = getDisappearingViews(recycler);
        Log.d(TAG, "disappearing views count = " + disappearingViews.size());

        if (disappearingViews.size() > 0) {
            /** we have some moving views
             * we should place it as disappearing to support predictive animations
             * we can't place all possible moves on theirs real place, because concrete layout position of particular view depends on placing of previous views
             * and there could be moving from 0 position to 10k. But it is preferably to place nearest moved view to real positions to make moving more natural
             * like moving from 0 position to 15 for example, where user could scroll fast and check
             */

            Log.d(TAG, "fill disappearing views");
            //so we fill additional rows to cover nearest moves
            layouterFactory.setAdditionalRowsCount(5);
            AnchorViewState highestViewState = anchorFactory.createAnchorState(highestView);
            AnchorViewState lowestViewState = anchorFactory.createAnchorState(lowestView);

            layouterFactory.setLayouterListener(layouter -> {
                for (Item item : layouter.getCurrentRowItems()) {
                    disappearingViews.remove(item.getViewPosition());
                }
            });

            ILayouter upLayouter = layouterFactory.getDisappearingUpLayouter(highestViewState.getAnchorViewRect());
            //todo layouting could reset view in scrap here, which we need actually place for predictive animations
            fillWithLayouter(recycler, upLayouter, highestViewState.getPosition() - 1);

            ILayouter downLayouter = layouterFactory.getDisappearingDownLayouter(lowestViewState.getAnchorViewRect());
            fillWithLayouter(recycler, downLayouter, lowestViewState.getPosition());

            Log.d(TAG, "AFTER disappearing views count = " + disappearingViews.size());

            downLayouter = layouterFactory.createInfiniteLayouter(downLayouter);

            //we should layout disappearing views left somewhere, just continue layout them in current layouter
            for (int i = 0; i< disappearingViews.downViews.size(); i++) {
                int position = disappearingViews.downViews.keyAt(i);
                downLayouter.placeView(recycler.getViewForPosition(position));
            }
            //layout last row
            downLayouter.layoutRow();

            upLayouter = layouterFactory.createInfiniteLayouter(upLayouter);
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

    private Rect containsInVisibleRow(View view) {
        int left = getDecoratedLeft(view) + 1;
        int top = getDecoratedTop(view) + 1;
        for (Rect rect : visibleRowsMap.keySet()) {
            if (rect.contains(left, top)) {
                return rect;
            }
        }
        throw new IllegalStateException("can't find view in visible rows");
    }

    /** during pre-layout calculate approximate height which will be free after moving items offscreen (removed or moved) */
    int calcDisappearingViewsHeight(RecyclerView.Recycler recycler) {
        int removedHeight = 0;

        HashMap<Rect, Integer> highestDeletedViewInRowMap = new HashMap<>();

        for (View view : childViews) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();

            boolean probablyMovedFromScreen = false;

            if (!lp.isItemRemoved()) {
                //view won't be removed, but maybe it moved offscreen
                int pos = lp.getViewLayoutPosition();
                int lowestPosition = highestView == null? Integer.MIN_VALUE : getPosition(highestView);
                int highestPosition = lowestView == null? Integer.MAX_VALUE : getPosition(lowestView);

                pos = recycler.convertPreLayoutPositionToPostLayout(pos);
                probablyMovedFromScreen = pos < lowestPosition || pos > highestPosition;
            }

            if (lp.isItemRemoved() || probablyMovedFromScreen) {
                deletingItemsOnScreenCount++;
                Rect rowRect = containsInVisibleRow(view);
                Integer maxHeight = highestDeletedViewInRowMap.get(rowRect);
                maxHeight = maxHeight == null ? 0 : maxHeight;
                int viewHeight = getDecoratedMeasuredHeight(view);
                highestDeletedViewInRowMap.put(rowRect, Math.max(maxHeight, viewHeight));
            }
        }

        for (Integer integer : highestDeletedViewInRowMap.values()) {
            removedHeight += integer;
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
        fill(recycler, createLayouterFactory(), anchorView);
    }

    /**
     * place all views on theirs right places according to current state
     */
    private void fill(RecyclerView.Recycler recycler, AbstractLayouterFactory layouterFactory, @NonNull AnchorViewState anchorView) {
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
        fillWithLayouter(recycler, upLayouter, startingPos - 1);
        ILayouter downLayouter = layouterFactory.getDownLayouter(anchorRect);
        fillWithLayouter(recycler, downLayouter, startingPos);

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
    }

//    RecyclerView.Recycler recycler;
//
//    public void removeAndRecycleView(View view) {
//        removeAndRecycleView(view, recycler);
//    }

    /**
     * @return true if RTL mode enabled in RecyclerView
     */
    private boolean isLayoutRTL() {
        return getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    /**
     * place views in layout started from chosen position with chosen layouter
     */
    private void fillWithLayouter(RecyclerView.Recycler recycler, ILayouter layouter, int startingPos) {
        AbstractPositionIterator iterator = layouter.positionIterator();
        //start from anchor position
        iterator.move(startingPos);
        logger.onStartLayouter(startingPos);

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

        fill(recycler, anchorView);
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
            Log.d(TAG, "normalization, position = " + cacheNormalizationPosition + " top view position = " + topViewPosition);
            Log.d(TAG, "top view top = " + getDecoratedTop(topView));
            Log.d(TAG, "top view bottom = " + getDecoratedBottom(topView));
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
            Log.e("span layout manager", "Cannot scroll to " + position + ", item count " + getItemCount());
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
            Log.e("span layout manager", "Cannot scroll to " + position + ", item count " + getItemCount());
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

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        adapterActionsLogger.onItemsAdded(positionStart, itemCount);
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        onLayoutUpdatedFromPosition(positionStart);
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        adapterActionsLogger.onItemsChanged();
        super.onItemsChanged(recyclerView);
        viewPositionsStorage.purge();
        onLayoutUpdatedFromPosition(0);
    }

    @Override
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        adapterActionsLogger.onItemsUpdated(positionStart, itemCount);
        super.onItemsUpdated(recyclerView, positionStart, itemCount);
        onLayoutUpdatedFromPosition(positionStart);
    }

    @Override
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
        onItemsUpdated(recyclerView, positionStart, itemCount);
    }

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
