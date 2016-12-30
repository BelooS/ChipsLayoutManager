package com.beloo.widget.chipslayoutmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;

public interface IScrollingController {

    RecyclerView.SmoothScroller createSmoothScroller(@NonNull Context context, int position, int timeMs, AnchorViewState anchor);

    boolean canScrollVertically();

    boolean canScrollHorizontally();

    /**
     * calculate offset of views while scrolling, layout items on new places
     */
    int scrollVerticallyBy(int d, RecyclerView.Recycler recycler, RecyclerView.State state);

    int scrollHorizontallyBy(int d, RecyclerView.Recycler recycler, RecyclerView.State state);

    /** changes may cause gaps on the UI, try to fix them */
    boolean normalizeGaps(RecyclerView.Recycler recycler, RecyclerView.State state);

    /** @see ChipsLayoutManager#computeVerticalScrollOffset(RecyclerView.State) */
    int computeVerticalScrollOffset(RecyclerView.State state);

    /** @see ChipsLayoutManager#computeVerticalScrollExtent(RecyclerView.State) */
    int computeVerticalScrollExtent(RecyclerView.State state);

    /** @see ChipsLayoutManager#computeVerticalScrollRange(RecyclerView.State) */
    int computeVerticalScrollRange(RecyclerView.State state);

    /** @see ChipsLayoutManager#computeHorizontalScrollOffset(RecyclerView.State) */
    int computeHorizontalScrollOffset(RecyclerView.State state);

    /** @see ChipsLayoutManager#computeHorizontalScrollExtent(RecyclerView.State) */
    int computeHorizontalScrollExtent(RecyclerView.State state);

    /** @see ChipsLayoutManager#computeHorizontalScrollRange(RecyclerView.State) */
    int computeHorizontalScrollRange(RecyclerView.State state);
}
