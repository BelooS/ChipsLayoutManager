package com.beloo.widget.spanlayoutmanager.layouter;

import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
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
    int currentViewBottom;
    private int currentViewPosition;
    List<Pair<Rect, View>> rowViews = new LinkedList<>();
    /** max bottom of current row views*/
    int viewBottom;
    int viewTop;

    int rowSize = 0;
    int previousRowSize;

    private ChipsLayoutManager layoutManager;
    private IViewCacheStorage cacheStorage;

    private IChildGravityResolver childGravityResolver;
    private GravityModifiersFactory gravityModifiersFactory = new GravityModifiersFactory();

    AbstractLayouter(ChipsLayoutManager layoutManager, int topOffset, int bottomOffset, IViewCacheStorage cacheStorage, IChildGravityResolver childGravityResolver) {
        this.layoutManager = layoutManager;
        this.viewTop = topOffset;
        this.viewBottom = bottomOffset;
        this.cacheStorage = cacheStorage;
        this.childGravityResolver = childGravityResolver;
    }

    int getCanvasWidth() {
        return layoutManager.getWidth();
    }

    int getCanvasHeight() {
        return layoutManager.getHeight();
    }

    public int getCurrentViewPosition() {
        return currentViewPosition;
    }

    /** read view params to memory */
    private void calculateView(View view) {
        currentViewHeight = layoutManager.getDecoratedMeasuredHeight(view);
        currentViewWidth = layoutManager.getDecoratedMeasuredWidth(view);
        currentViewBottom = layoutManager.getDecoratedBottom(view);
        currentViewPosition = layoutManager.getPosition(view);
    }

    abstract void loadFromCache(@NonNull Rect rect);

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

        Rect rect = cacheStorage.getRect(currentViewPosition);
        if (rect != null) {
            loadFromCache(rect);
        } else {
            rect = createViewRect(view);
        }

        rowViews.add(new Pair<>(rect, view));

        cacheStorage.put(rect, currentViewPosition);

        return true;
    }

    /** factory method for Rect, where view will be placed. Creation based on inner layouter parameters */
    abstract Rect createViewRect(View view);

    @Override
    public int getPreviousRowSize() {
        return previousRowSize;
    }

    @CallSuper
    @Override
    /** Read layouter state from current attached view. We need only last of it, but we can't determine here which is last.
     * Based on characteristics of last attached view, layouter algorithm will be able to continue placing from it.
     * This method have to be called on attaching view*/
    public boolean onAttachView(View view) {
        rowSize++;

        if (isFinishedLayouting()) return false;

        layoutManager.attachView(view);
        return true;
    }

    @CallSuper
    @Override
    /** add views from current row to layout*/
    public void layoutRow() {
        previousRowSize = rowSize;
        this.rowSize = 0;
    }

    /** layout pre-calculated row on a recyclerView canvas
     * @param leftOffsetOfRow How much row have to be shifted before placing. Should be negative on RTL
     * returns viewTop */
    int layoutRow(List<Pair<Rect, View>> rowViews, int minTop, int maxBottom, int leftOffsetOfRow) {
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
            int viewGravity = childGravityResolver.getItemGravity(getLayoutManager().getPosition(view));
            IGravityModifier gravityModifier = gravityModifiersFactory.getGravityModifier(viewGravity);
            gravityModifier.modifyChildRect(minTop, maxBottom, viewRect);

            addView(view);

            //layout whole views in a row
            layoutManager.layoutDecorated(view, viewRect.left, viewRect.top, viewRect.right, viewRect.bottom);
        }

        return minTop;
    }

    abstract void addView(View view);


    ChipsLayoutManager getLayoutManager() {
        return layoutManager;
    }
}
