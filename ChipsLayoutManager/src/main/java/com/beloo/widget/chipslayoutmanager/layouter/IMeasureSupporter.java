package com.beloo.widget.chipslayoutmanager.layouter;

import android.support.v7.widget.RecyclerView;

public interface IMeasureSupporter {
    void onItemsRemoved(RecyclerView recyclerView);

    void afterOnLayoutChildren();

    void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec);
}
