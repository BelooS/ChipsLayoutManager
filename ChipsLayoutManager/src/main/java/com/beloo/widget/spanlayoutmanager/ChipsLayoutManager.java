package com.beloo.widget.spanlayoutmanager;

import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.beloo.widget.spanlayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.spanlayoutmanager.cache.ViewCacheFactory;
import com.beloo.widget.spanlayoutmanager.gravity.CenterChildGravity;
import com.beloo.widget.spanlayoutmanager.gravity.CustomGravityResolver;
import com.beloo.widget.spanlayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.spanlayoutmanager.layouter.AbstractPositionIterator;
import com.beloo.widget.spanlayoutmanager.layouter.ILayouter;
import com.beloo.widget.spanlayoutmanager.layouter.LayouterFactory;

public class ChipsLayoutManager extends RecyclerView.LayoutManager implements IChipsLayoutManagerContract {

    private IChildGravityResolver childGravityResolver;

    /**
     * coefficient to support fast scrolling, caching views only for one row may not be enough
     */
    private static final float FAST_SCROLLING_COEFFICIENT = 2;
    private int maxViewsInRow = 2;
    private LayouterFactory layouterFactory;
    private IViewCacheStorage viewPositionsStorage;

    private SparseArray<View> viewCache = new SparseArray<>();

    private Integer anchorViewPosition = null;

    /**
     * highest top position of attached views
     */
    private int highestViewTop = Integer.MAX_VALUE;

    /**
     * stored current anchor view due to scroll state changes
     */
    private AnchorViewState anchorView = AnchorViewState.getNotFoundState();

    private boolean isScrollingEnabled = true;

    private ChipsLayoutManager() {
        viewPositionsStorage = new ViewCacheFactory(this).createCacheStorage();
        layouterFactory = new LayouterFactory(this, viewPositionsStorage);

        setAutoMeasureEnabled(true);
        setMeasurementCacheEnabled(true);
    }

    public static Builder newBuilder() {
        return new ChipsLayoutManager().new Builder();
    }

    public IChildGravityResolver getChildGravityResolver() {
        return childGravityResolver;
    }

    public void setScrollingEnabled(boolean isEnabled) {
        isScrollingEnabled = isEnabled;
    }

    public class Builder {

        @SpanLayoutChildGravity
        private Integer gravity;

        private Builder(){}

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

        public Builder setScrollingEnabled(boolean isEnabled) {
            ChipsLayoutManager.this.setScrollingEnabled(isEnabled);
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
        removeAllViews();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SLMParcelableContainer container = (SLMParcelableContainer) state;
        anchorView = container.getAnchorViewState();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return new SLMParcelableContainer(getAnchorVisibleTopLeftView());
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //We have nothing to show for an empty data set but clear any existing views
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        calcRecyclerCacheSize(recycler, 2);

        if (!state.isPreLayout()) {
            if (anchorView.isNotFoundState()) {
                //try to found anchorView here
                anchorView = getAnchorVisibleTopLeftView();
            }
            detachAndScrapAttachedViews(recycler);

            if (!anchorView.isNotFoundState() && anchorViewPosition != null && anchorViewPosition == 0) {
                //we can't add view in a hidden area if added view inserted on a zero position. so needed workaround here, we reset anchor position to 0
                //for properly insertion only
                fill(recycler, anchorView, anchorViewPosition);
                anchorViewPosition = null;
            } else {
                fill(recycler, anchorView);
            }
        } else {
            anchorView = getAnchorVisibleTopLeftView();
            if (!anchorView.isNotFoundState())
                anchorViewPosition = anchorView.getPosition();
        }
    }

    @Override
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        viewPositionsStorage.purgeCacheFromPosition(positionStart);
        viewPositionsStorage.setCachingEnabled(false);
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        viewPositionsStorage.purgeCacheFromPosition(positionStart);
        viewPositionsStorage.setCachingEnabled(false);
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        super.onItemsChanged(recyclerView);
        viewPositionsStorage.purge();
        viewPositionsStorage.setCachingEnabled(false);
    }

    @Override
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount);
        viewPositionsStorage.purgeCacheFromPosition(positionStart);
        viewPositionsStorage.setCachingEnabled(false);
    }

    //todo test with moving
    @Override
    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        super.onItemsMoved(recyclerView, from, to, itemCount);
        viewPositionsStorage.purgeCacheFromPosition(from);
        viewPositionsStorage.purgeCacheFromPosition(to);
        viewPositionsStorage.setCachingEnabled(false);
    }

    @Override
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
        onItemsUpdated(recyclerView, positionStart, itemCount);
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollingEnabled;
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    private void fill(RecyclerView.Recycler recycler, @NonNull AnchorViewState anchorView) {
        int anchorPos = 0;
        if (!anchorView.isNotFoundState()) {
            anchorPos = anchorView.getPosition();
        }

        fill(recycler, anchorView, anchorPos);
    }

    private void fill(RecyclerView.Recycler recycler, @NonNull AnchorViewState anchorView, int startingPos) {

        Rect anchorRect = anchorView.getAnchorViewRect();

        highestViewTop = Integer.MAX_VALUE;
        viewCache.clear();

        //place all added views to cache...
        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            viewCache.put(pos, view);
        }

        //... and remove from layout
        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }

        ILayouter downLayouter = layouterFactory.getDownLayouter(anchorRect.top, anchorRect.left, anchorRect.bottom, anchorRect.right, isLayoutRTL());
        fillWithLayouter(recycler, downLayouter, startingPos);
        ILayouter upLayouter = layouterFactory.getUpLayouter(Math.min(anchorRect.top, highestViewTop), anchorRect.left, anchorRect.bottom, anchorRect.right, isLayoutRTL());
        fillWithLayouter(recycler, upLayouter, startingPos - 1);

        //move to trash everything, which haven't used in this layout cycle
        //that views gone from a screen or was removed outside from adapter
        int recycledSize = viewCache.size();
        for (int i = 0; i < viewCache.size(); i++) {
            removeAndRecycleView(viewCache.valueAt(i), recycler);
            Log.d("fillWithLayouter", "recycle position =" + viewCache.keyAt(i));
        }

        Log.d("fillWithLayouter", "recycled count = " + recycledSize);
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
    private void fillWithLayouter(RecyclerView.Recycler recycler, ILayouter layouter, int startingPos) {

        AbstractPositionIterator iterator = layouter.positionIterator();
        iterator.move(startingPos);

        int requestedItems = 0;
        int recycledItems = 0;
        int startCacheSize = viewCache.size();
        Log.d("fillWithLayouter", "cached items = " + startCacheSize);

        while (iterator.hasNext()) {
            int pos = iterator.next();
            View view = viewCache.get(pos);
            if (view == null) {
                Log.i("fillWithLayouter", "getView for position = " + pos);
                view = recycler.getViewForPosition(pos);
                requestedItems++;
                measureChildWithMargins(view, 0, 0);

                if (!layouter.placeView(view)) {
                     /* reached end of visible bounds, exit.
                    recycle view, which was requested previously
                     */
                    recycler.recycleView(view);
                    recycledItems++;
                    break;
                }

            } else {
                if (!layouter.onAttachView(view)) {
                    break;
                }

                //fillWithLayouter down
                highestViewTop = Math.min(highestViewTop, getDecoratedTop(view));

                viewCache.remove(pos);

            }

        }

        Log.d("fillWithLayouter", "reattached items = " + (startCacheSize - viewCache.size() + " : requested items = " + requestedItems + " recycledItems = " + recycledItems));

        //layout last row
        layouter.layoutRow();
    }

    /**
     * recycler should contain all recycled views from a longest row, not just 2 holders by default
     */
    private void calcRecyclerCacheSize(RecyclerView.Recycler recycler, int rowSize) {
        maxViewsInRow = Math.max(rowSize, maxViewsInRow);
        recycler.setViewCacheSize((int) (maxViewsInRow * FAST_SCROLLING_COEFFICIENT));
    }

    /**
     * calculate offset of views while scrolling, layout items on new places
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        dy = scrollVerticallyInternal(dy);
        offsetChildrenVertical(-dy);
        anchorView = getAnchorVisibleTopLeftView();
        View topView = getChildAt(0);

        if (!anchorView.isNotFoundState() && getPosition(topView) == 0) {
            viewPositionsStorage.purge();
            postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                    requestSimpleAnimationsInNextLayout();
                }
            });

        }

        fill(recycler, anchorView);
        return dy;
    }

    private int scrollVerticallyInternal(int dy) {
        int childCount = getChildCount();
        int itemCount = getItemCount();
        if (childCount == 0) {
            return 0;
        }

        final View topView = getChildAt(0);
        int topViewPosition = getPosition(topView);
        final View bottomView = getChildAt(childCount - 1);

        int viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView);
        if (getPosition(topView) == 0 && getPosition(bottomView) == getItemCount() - 1 && viewSpan <= getHeight()) {
            //where all objects fit on screen, no scrolling needed
            return 0;
        }

        int delta = 0;
        if (dy < 0) {   //if content scrolled down
            if (viewPositionsStorage.isInCache(topViewPosition) || topViewPosition == 0) {
                viewPositionsStorage.setCachingEnabled(true);
            }

            //todo workaround. somehow in the first row view in getChildAt(0) can have position 1
            boolean isZeroAdded = false;
            for (int i = 0; i < childCount; i++) {
                View test = getChildAt(i);
                if (getPosition(test) == 0) {
                    isZeroAdded = true;
                }
            }

            if (!isZeroAdded) { //in case 0 position haven't added in layout yet
                delta = dy;
            } else { //in case top view is a first view in adapter and wouldn't be any other view above
                View view = findTopView();
                int viewTop = getDecoratedTop(view);
                delta = Math.max(viewTop, dy);
            }
        } else if (dy > 0) { //if content scrolled up
            View lastView = getChildAt(childCount - 1);
            int lastViewAdapterPos = getPosition(lastView);
            if (lastViewAdapterPos < itemCount - 1) { //in case lower view isn't the last view in adapter
                delta = dy;
            } else { //in case lower view is the last view in adapter and wouldn't be any other view below
                int viewBottom = getDecoratedBottom(lastView);
                int parentBottom = getHeight();
                delta = Math.min(viewBottom - parentBottom, dy);
            }
        }
        return delta;
    }

    /**
     * find top view in layout
     */
    private View findTopView() {
        View topView = getChildAt(0);
        int minTop = getDecoratedTop(topView);
        int childCount = getChildCount();
        for (int i = 1; i < childCount; i++) {
            View view = getChildAt(i);
            int top = getDecoratedTop(view);
            if (top < minTop) {
                topView = view;
            }
        }
        return topView;
    }

    @NonNull
    /** find the view in a higher row which is closest to the left border*/
    private AnchorViewState getAnchorVisibleTopLeftView() {
        int childCount = getChildCount();
        AnchorViewState topLeft = AnchorViewState.getNotFoundState();

        Rect mainRect = new Rect(0, 0, getWidth(), getHeight());
        int minTop = Integer.MAX_VALUE;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int top = getDecoratedTop(view);
            int bottom = getDecoratedBottom(view);
            int left = getDecoratedLeft(view);
            int right = getDecoratedRight(view);
            Rect viewRect = new Rect(left, top, right, bottom);
            boolean intersect = viewRect.intersect(mainRect);
            if (intersect) {
                if (getPosition(view) != -1) {
                    if (topLeft.isNotFoundState()) {
                        topLeft = new AnchorViewState(getPosition(view), new Rect(left, top, right, bottom));
                    }
                    minTop = Math.min(minTop, top);
                }
            }
        }

        if (!topLeft.isNotFoundState()) {
            topLeft.getAnchorViewRect().top = minTop;
        }

        return topLeft;
    }

    public void scrollToPosition(int position) {
        if (position >= getItemCount()) {
            Log.e("span layout manager", "Cannot scroll to " + position + ", item count " + getItemCount());
            return;
        }

        //Trigger a new view layout
        requestLayout();
    }

}
