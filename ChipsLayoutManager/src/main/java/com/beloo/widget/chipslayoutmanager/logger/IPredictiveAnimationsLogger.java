package com.beloo.widget.chipslayoutmanager.logger;

import android.support.v7.widget.RecyclerView;

public interface IPredictiveAnimationsLogger {
    void onSummarizedDeletingItemsHeightCalculated(int height);
    void heightOfCanvas(RecyclerView.LayoutManager layoutManager);
    void logState(RecyclerView.State state);
}
