package com.beloo.widget.chipslayoutmanager;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.layouter.ICanvas;
import com.beloo.widget.chipslayoutmanager.layouter.IStateFactory;

import java.util.List;

class DisappearingViewsManager implements IDisappearingViewsManager {

    private ICanvas canvas;
    private ChildViewsIterable childViews;
    private IStateFactory stateFactory;

    /* in pre-layouter drawing we need item count with items will be actually deleted to pre-draw appearing items properly
    * buf value */
    private int deletingItemsOnScreenCount;

    DisappearingViewsManager(ICanvas canvas, ChildViewsIterable childViews, IStateFactory stateFactory) {
        this.canvas = canvas;
        this.childViews = childViews;
        this.stateFactory = stateFactory;
    }

    class DisappearingViewsContainer {
        private SparseArray<View> backwardViews = new SparseArray<>();
        private SparseArray<View> forwardViews = new SparseArray<>();

        int size() {
            return backwardViews.size() + forwardViews.size();
        }

        SparseArray<View> getBackwardViews() {
            return backwardViews;
        }

        SparseArray<View> getForwardViews() {
            return forwardViews;
        }
    }

    /** @return views which moved from screen, but not deleted*/
    @Override
    public DisappearingViewsContainer getDisappearingViews(RecyclerView.Recycler recycler) {
        final List<RecyclerView.ViewHolder> scrapList = recycler.getScrapList();
        DisappearingViewsContainer container = new DisappearingViewsContainer();

        for (RecyclerView.ViewHolder holder : scrapList) {
            final View child = holder.itemView;
            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (!lp.isItemRemoved()) {
                if (lp.getViewAdapterPosition() < canvas.getMinPositionOnScreen()) {
                    container.backwardViews.put(lp.getViewAdapterPosition(), child);
                } else if (lp.getViewAdapterPosition() > canvas.getMaxPositionOnScreen()) {
                    container.forwardViews.put(lp.getViewAdapterPosition(), child);
                }
            }
        }

        return container;
    }
    /** during pre-layout calculate approximate height which will be free after moving items offscreen (removed or moved)
     * @return approximate height of disappearing views. Could be bigger, than accurate value. */
    @Override
    public int calcDisappearingViewsLength(RecyclerView.Recycler recycler) {
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
                probablyMovedFromScreen = pos < canvas.getMinPositionOnScreen() || pos > canvas.getMaxPositionOnScreen();
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

    @Override
    public int getDeletingItemsOnScreenCount() {
        return deletingItemsOnScreenCount;
    }

    @Override
    public void reset() {
        deletingItemsOnScreenCount = 0;
    }
}
