package com.beloo.widget.chipslayoutmanager;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;
import com.beloo.widget.chipslayoutmanager.layouter.ICanvas;
import com.beloo.widget.chipslayoutmanager.layouter.IStateFactory;

abstract class ScrollingController implements IScrollingController {

    private ChipsLayoutManager lm;
    private IScrollerListener scrollerListener;
    private IStateFactory stateFactory;
    ICanvas canvas;

    interface IScrollerListener {
        void onScrolled(IScrollingController scrollingController, RecyclerView.Recycler recycler, RecyclerView.State state);
    }

    ScrollingController(ChipsLayoutManager layoutManager, IStateFactory stateFactory, IScrollerListener scrollerListener) {
        this.lm = layoutManager;
        this.scrollerListener = scrollerListener;
        this.stateFactory = stateFactory;
        this.canvas = layoutManager.getCanvas();
    }

    final int calculateEndGap() {
        if (lm.getChildCount() == 0) return 0;

        int visibleViewsCount = lm.getCompletelyVisibleViewsCount();

        if (visibleViewsCount == lm.getItemCount()) return 0;
        int currentEnd = stateFactory.getEndViewBound();
        int desiredEnd = stateFactory.getEndAfterPadding();

        int diff = desiredEnd - currentEnd;
        if (diff < 0) return 0;
        return diff;
    }

    final int calculateStartGap() {
        if (lm.getChildCount() == 0) return 0;
        int currentStart = stateFactory.getStartViewBound();
        int desiredStart = stateFactory.getStartAfterPadding();
        int diff = currentStart - desiredStart;
        if (diff < 0) return 0;
        return diff;
    }

    @Override
    public final boolean normalizeGaps(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int backwardGap = calculateStartGap();
        if (backwardGap > 0) {
            offsetChildren(-backwardGap);
            //if we have normalized start gap, normalizing bottom have no sense
            return true;
        }

        int forwardGap = calculateEndGap();
        if (forwardGap > 0) {
            scrollBy(-forwardGap, recycler, state);
            return true;
        }

        return false;
    }

    final int calcOffset(int d) {
        int childCount = lm.getChildCount();
        if (childCount == 0) {
            return 0;
        }

        int delta = 0;
        if (d < 0) {   //if content scrolled down
            delta = onContentScrolledBackward(d);
        } else if (d > 0) { //if content scrolled up
            delta = onContentScrolledForward(d);
        }

        return delta;
    }

    /**
     * invoked when content scrolled forward (return to older items)
     *
     * @param d not processed changing of x or y axis, depending on lm state
     * @return delta. Calculated changing of x or y axis, depending on lm state
     */
    final int onContentScrolledBackward(int d) {
        int delta;

        AnchorViewState anchor = lm.getAnchor();
        if (anchor.getAnchorViewRect() == null) {
            return 0;
        }

        if (anchor.getPosition() != 0) { //in case 0 position haven't added in layout yet
            delta = d;
        } else { //in case top view is a first view in adapter and wouldn't be any other view above
            int startBorder = stateFactory.getStartAfterPadding();
            int viewStart = stateFactory.getStart(anchor);
            int distance;
            distance = viewStart - startBorder;

            if (distance >= 0) {
                // in case over scroll on top border
                delta = distance;
            } else {
                //in case first child showed partially
                delta = Math.max(distance, d);
            }
        }

        return delta;
    }

    /**
     * invoked when content scrolled up (to newer items)
     *
     * @param d not processed changing of x or y axis, depending on lm state
     * @return delta. Calculated changing of x or y axis, depending on lm state
     */
    final int onContentScrolledForward(int d) {
        int childCount = lm.getChildCount();
        int itemCount = lm.getItemCount();
        int delta;

        View lastView = lm.getChildAt(childCount - 1);
        int lastViewAdapterPos = lm.getPosition(lastView);
        if (lastViewAdapterPos < itemCount - 1) { //in case lower view isn't the last view in adapter
            delta = d;
        } else { //in case lower view is the last view in adapter and wouldn't be any other view below
            int viewEnd = stateFactory.getEndViewBound();
            int parentEnd = stateFactory.getEndAfterPadding();
            delta = Math.min(viewEnd - parentEnd, d);
        }

        return delta;
    }

    abstract void offsetChildren(int d);

    @Override
    public final int scrollHorizontallyBy(int d, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return canScrollHorizontally()? scrollBy(d, recycler, state) : 0;
    }

    @Override
    public final int scrollVerticallyBy(int d, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return canScrollVertically()? scrollBy(d, recycler, state) : 0;
    }

    private int scrollBy(int d, RecyclerView.Recycler recycler, RecyclerView.State state) {
        d = calcOffset(d);
        offsetChildren(-d);

        scrollerListener.onScrolled(this, recycler, state);

        return d;
    }

    private int getLaidOutArea() {
        return stateFactory.getEndViewBound() -
                stateFactory.getStartViewBound();
    }

    /** @see ChipsLayoutManager#computeVerticalScrollOffset(RecyclerView.State)
     * @see ChipsLayoutManager#computeHorizontalScrollOffset(RecyclerView.State) */
    private int computeScrollOffset(RecyclerView.State state) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0) {
            return 0;
        }

        int firstVisiblePos = lm.findFirstVisibleItemPosition();
        int lastVisiblePos = lm.findLastVisibleItemPosition();
        final int itemsBefore = Math.max(0, firstVisiblePos);

        if (!lm.isSmoothScrollbarEnabled()) {
            return itemsBefore;
        }

        final int itemRange = Math.abs(firstVisiblePos - lastVisiblePos) + 1;

        final float avgSizePerRow = (float) getLaidOutArea() / itemRange;

        return Math.round(itemsBefore * avgSizePerRow +
                (stateFactory.getStartAfterPadding() - stateFactory.getStartViewBound()));
    }

    /** @see ChipsLayoutManager#computeVerticalScrollExtent(RecyclerView.State)
     * @see ChipsLayoutManager#computeHorizontalScrollExtent(RecyclerView.State) */
    private int computeScrollExtent(RecyclerView.State state) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0) {
            return 0;
        }

        int firstVisiblePos = lm.findFirstVisibleItemPosition();
        int lastVisiblePos = lm.findLastVisibleItemPosition();

        if (!lm.isSmoothScrollbarEnabled()) {
            return Math.abs(lastVisiblePos - firstVisiblePos) + 1;
        }

        return Math.min(stateFactory.getTotalSpace(), getLaidOutArea());
    }

    private int computeScrollRange(RecyclerView.State state) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0) {
            return 0;
        }

        if (!lm.isSmoothScrollbarEnabled()) {
            return state.getItemCount();
        }

        int firstVisiblePos = lm.findFirstVisibleItemPosition();
        int lastVisiblePos = lm.findLastVisibleItemPosition();

        // smooth scrollbar enabled. try to estimate better.
        final int laidOutRange = Math.abs(firstVisiblePos - lastVisiblePos) + 1;

        // estimate a size for full list.
        return (int) ((float) getLaidOutArea() / laidOutRange * state.getItemCount());
    }

    @Override
    public final int computeVerticalScrollExtent(RecyclerView.State state) {
        return canScrollVertically() ? computeScrollExtent(state) : 0;
    }

    @Override
    public final int computeVerticalScrollRange(RecyclerView.State state) {
        return canScrollVertically() ? computeScrollRange(state) : 0;
    }

    @Override
    public final int computeVerticalScrollOffset(RecyclerView.State state) {
        return canScrollVertically() ? computeScrollOffset(state) : 0;
    }

    @Override
    public final int computeHorizontalScrollRange(RecyclerView.State state) {
        return canScrollHorizontally() ? computeScrollRange(state) : 0;
    }

    @Override
    public final int computeHorizontalScrollOffset(RecyclerView.State state) {
        return canScrollHorizontally() ? computeScrollOffset(state) : 0;
    }

    @Override
    public final int computeHorizontalScrollExtent(RecyclerView.State state) {
        return canScrollHorizontally() ? computeScrollExtent(state) : 0;
    }
}
