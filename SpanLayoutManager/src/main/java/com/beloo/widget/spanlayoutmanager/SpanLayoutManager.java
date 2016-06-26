package com.beloo.widget.spanlayoutmanager;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.BuildConfig;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.beloo.widget.spanlayoutmanager.gravityModifier.GravityModifiersFactory;
import com.beloo.widget.spanlayoutmanager.gravityModifier.IGravityModifier;
import com.beloo.widget.spanlayoutmanager.layouter.AbstractPositionIterator;
import com.beloo.widget.spanlayoutmanager.layouter.ILayouter;
import com.beloo.widget.spanlayoutmanager.layouter.LayouterFactory;

public class SpanLayoutManager extends RecyclerView.LayoutManager {

    private IChildGravityResolver childGravityResolver = new CenterChildGravity();
    private GravityModifiersFactory gravityModifiersFactory = new GravityModifiersFactory();

    /** coefficient to support fast scrolling, caching views only for one row may not be enough */
    private static final float FAST_SCROLLING_COEFFICIENT = 2;
    private int maxViewsInRow = 2;
    private LayouterFactory layouterFactory = new LayouterFactory(this);

    private SparseArray<View> viewCache = new SparseArray<>();

    private Integer anchorViewPosition = null;

    /** highest top position of attached views*/
    private int highestViewTop = Integer.MAX_VALUE;

    public void setChildGravityResolver(IChildGravityResolver childGravityResolver) {
        this.childGravityResolver = childGravityResolver;
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
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //We have nothing to show for an empty data set but clear any existing views
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        calcRecyclerCacheSize(recycler, 2);

        View anchorView = getAnchorVisibleTopLeftView();
        if (!state.isPreLayout()) {
            detachAndScrapAttachedViews(recycler);

            if (anchorView != null && anchorViewPosition != null && anchorViewPosition == 0) {
                //we can't add view in a hidden area if added view inserted on a zero position. so needed workaround here, we reset anchor position to 0
                //for properly insertion only
                fill(recycler, anchorView, anchorViewPosition);
                anchorViewPosition = null;
            } else {
                fill(recycler, anchorView);
            }
        } else {
            if (anchorView != null)
                anchorViewPosition = getPosition(anchorView);

        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    private void fill(RecyclerView.Recycler recycler, @Nullable View anchorView) {
        int anchorPos = 0;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
        }

        fill(recycler, anchorView, anchorPos);
    }

    private void fill(RecyclerView.Recycler recycler, @Nullable View anchorView, int startingPos) {

        int anchorTop = 0;
        int anchorBottom = 0;
        int anchorLeft = 0;
        int anchorRight = 0;
        if (anchorView != null) {
            anchorTop = getDecoratedTop(anchorView);
            anchorBottom = getDecoratedBottom(anchorView);
            anchorLeft = getDecoratedLeft(anchorView);
            anchorRight = getDecoratedRight(anchorView);
        }

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

        ILayouter downLayouter = layouterFactory.getDownLayouter(anchorTop, anchorLeft, anchorBottom, anchorRight, isLayoutRTL());
        fillWithLayouter(recycler, downLayouter, startingPos);
        ILayouter upLayouter = layouterFactory.getUpLayouter(Math.min(anchorTop, highestViewTop), anchorLeft, anchorBottom, anchorRight, isLayoutRTL());
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

    protected boolean isLayoutRTL() {
        return getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

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

                layouter.calculateView(view);

                if (layouter.canNotBePlacedInCurrentRow()) {
                    layouter.layoutRow();
                }

                if (layouter.isFinishedLayouting()) {
                    /* reached end of visible bounds, exit.
                    recycle view, which was requested previously
                     */
                    recycler.recycleView(view);
                    recycledItems++;
                    break;
                }

                layouter.placeView(view);
            } else {
                layouter.onAttachView(view);

                if (layouter.isFinishedLayouting()) {
                    break;
                }

                attachView(view);

                //fillWithLayouter down
                highestViewTop = Math.min(highestViewTop, getDecoratedTop(view));

                viewCache.remove(pos);

            }

        }

        Log.d("fillWithLayouter", "reattached items = " + (startCacheSize - viewCache.size() + " : requested items = " + requestedItems + " recycledItems = " + recycledItems));

        layouter.layoutRow();
    }


    /** layout pre-calculated row on a recyclerView canvas
     * @param isReverseOrder if fillWithLayouter views from the end this flag have to be true to not break child position in recyclerView
     * @param leftOffsetOfRow How much row have to be shifted before placing. Should be negative on RTL
     * returns viewTop */
    public int layoutRow(List<Pair<Rect, View>> rowViews, int minTop, int maxBottom, int leftOffsetOfRow, boolean isReverseOrder) {
        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;

            viewRect.left = viewRect.left - leftOffsetOfRow;
            viewRect.right = viewRect.right - leftOffsetOfRow;

            minTop = Math.min(minTop, viewRect.top);
            maxBottom = Math.max(maxBottom, viewRect.bottom);
        }

        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;
            View view = rowViewRectPair.second;

            @SpanLayoutChildGravity
            int viewGravity = childGravityResolver.getItemGravity(getPosition(view));
            IGravityModifier gravityModifier = gravityModifiersFactory.getGravityModifier(viewGravity);
            gravityModifier.modifyChildRect(minTop, maxBottom, viewRect);

            if (isReverseOrder) {
                addView(view, 0);
            } else {
                addView(view);
            }

            List<? super String> list = new ArrayList<Object>();
            list.add("");

            //layout whole views in a row
            layoutDecorated(view, viewRect.left, viewRect.top, viewRect.right, viewRect.bottom);
        }

        return minTop;
    }

    /** recycler should contain all recycled views from a longest row, not just 2 holders by default*/
    private void calcRecyclerCacheSize(RecyclerView.Recycler recycler, int rowSize) {
        maxViewsInRow = Math.max(rowSize, maxViewsInRow);
        recycler.setViewCacheSize((int) (maxViewsInRow * FAST_SCROLLING_COEFFICIENT));
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
    }

    /** calculate offset of views while scrolling, layout items on new places*/
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        dy = scrollVerticallyInternal(dy);
        offsetChildrenVertical(-dy);
        View anchorView = getAnchorVisibleTopLeftView();
        if (anchorView != null && getPosition(anchorView) == 0) {
            //todo refactor it, blinking now without animation. Workaround to fix start position of items if some items have been added after initialization
            detachAndScrapAttachedViews(recycler);
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
        final View bottomView = getChildAt(childCount - 1);

        int viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView);
        if (getPosition(topView) == 0 && getPosition(bottomView) == getItemCount() - 1 && viewSpan <= getHeight()) {
            //where all objects fit on screen, no scrolling needed
            return 0;
        }

        int delta = 0;
        //if content scrolled down
        if (dy < 0) {
            View firstView = getChildAt(0);
            int firstViewAdapterPos = getPosition(firstView);
            if (firstViewAdapterPos > 0) { //если верхняя вюшка не самая первая в адаптере
                delta = dy;
            } else { //если верхняя вьюшка самая первая в адаптере и выше вьюшек больше быть не может
                int viewTop = getDecoratedTop(firstView);
                delta = Math.max(viewTop, dy);
            }
        } else if (dy > 0) { //if content scrolled up
            View lastView = getChildAt(childCount - 1);
            int lastViewAdapterPos = getPosition(lastView);
            if (lastViewAdapterPos < itemCount - 1) { //если нижняя вюшка не самая последняя в адаптере
                delta = dy;
            } else { //если нижняя вьюшка самая последняя в адаптере и ниже вьюшек больше быть не может
                int viewBottom = getDecoratedBottom(lastView);
                int parentBottom = getHeight();
                delta = Math.min(viewBottom - parentBottom, dy);
            }
        }
        return delta;
    }

    /**
     * @return View, which is highest visible left view
     */
    @Nullable
    private View getAnchorVisibleTopLeftView() {
        int childCount = getChildCount();
        View topLeft = null;

        Rect mainRect = new Rect(0, 0, getWidth(), getHeight());
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
                    topLeft = view;
                    break;
                }
            }
        }

        return topLeft;
    }

    public void scrollToPosition(int position) {
        if (position >= getItemCount()) {
            Log.e("span layout manager", "Cannot scroll to " + position + ", item count "+getItemCount());
            return;
        }

        //Trigger a new view layout
        requestLayout();
    }

}
