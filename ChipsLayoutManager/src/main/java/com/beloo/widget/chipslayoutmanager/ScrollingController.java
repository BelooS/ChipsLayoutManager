package com.beloo.widget.chipslayoutmanager;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;
import com.beloo.widget.chipslayoutmanager.layouter.ICanvas;
import com.beloo.widget.chipslayoutmanager.layouter.IStateFactory;
import com.beloo.widget.chipslayoutmanager.logger.IScrollingLogger;
import com.beloo.widget.chipslayoutmanager.logger.LoggerFactory;

abstract class ScrollingController implements IScrollingController {

    private ChipsLayoutManager layoutManager;
    private IScrollerListener scrollerListener;
    private IScrollingLogger scrollingLogger;
    private IStateFactory stateFactory;
    ICanvas canvas;

    interface IScrollerListener {
        void onScrolled(IScrollingController scrollingController, RecyclerView.Recycler recycler, RecyclerView.State state);
    }

    ScrollingController(ChipsLayoutManager layoutManager, IStateFactory stateFactory, IScrollerListener scrollerListener) {
        this.layoutManager = layoutManager;
        this.scrollerListener = scrollerListener;
        LoggerFactory loggerFactory = new LoggerFactory();
        scrollingLogger = loggerFactory.getScrollingLogger();
        this.stateFactory = stateFactory;
        this.canvas = layoutManager.getCanvas();
    }

    final int calculateEndGap() {
        if (layoutManager.getChildCount() == 0) return 0;
        int currentEnd = stateFactory.getEndViewBound();
        int desiredEnd = stateFactory.getEndAfterPadding();

        int diff = desiredEnd - currentEnd;
        if (diff < 0) return 0;
        return diff;
    }

    final int calculateStartGap() {
        if (layoutManager.getChildCount() == 0) return 0;
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
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return 0;
        }

        int delta = 0;
        if (d < 0) {   //if content scrolled down
            delta = onContentScrolledForward(d);
        } else if (d > 0) { //if content scrolled up
            delta = onContentScrolledBackward(d);
        }

        return delta;
    }

    /**
     * invoked when content scrolled forward (return to older items)
     *
     * @param d not processed changing of x or y axis, depending on lm state
     * @return delta. Calculated changing of x or y axis, depending on lm state
     */
    final int onContentScrolledForward(int d) {
        int delta;

        AnchorViewState anchor = layoutManager.getAnchor();
        if (anchor.getAnchorViewRect() == null) {
            return 0;
        }

        if (!layoutManager.getCanvas().isFirstItemAdded()) { //in case 0 position haven't added in layout yet
            delta = d;
        } else { //in case top view is a first view in adapter and wouldn't be any other view above
            int startBorder = stateFactory.getStartAfterPadding();
            int viewStart = stateFactory.getStart(anchor);
            int distance;
            distance = viewStart - startBorder;

            scrollingLogger.logUpScrollingNormalizationDistance(distance);

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
    final int onContentScrolledBackward(int d) {
        int childCount = layoutManager.getChildCount();
        int itemCount = layoutManager.getItemCount();
        int delta;

        View lastView = layoutManager.getChildAt(childCount - 1);
        int lastViewAdapterPos = layoutManager.getPosition(lastView);
        if (lastViewAdapterPos < itemCount - 1) { //in case lower view isn't the last view in adapter
            delta = d;
        } else { //in case lower view is the last view in adapter and wouldn't be any other view below
            int viewEnd = stateFactory.getEndViewBound();
            int parentEnd = stateFactory.getEnd();
            delta = Math.min(viewEnd - parentEnd, d);
        }

        return delta;
    }

    public abstract void offsetChildren(int d);

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
        scrollingLogger.logChildCount(layoutManager.getChildCount());

        scrollerListener.onScrolled(this, recycler, state);

        return d;
    }
}
