package com.beloo.widget.chipslayoutmanager.logger;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class PredictiveAnimationsLogger implements IPredictiveAnimationsLogger {

    @Override
    public void onSummarizedDeletingItemsHeightCalculated(int additionalHeight) {
        Log.d("onDeletingHeightCalc", "additional height  = " + additionalHeight);
    }

    @Override
    public void heightOfCanvas(RecyclerView.LayoutManager layoutManager) {
        Log.d("LayoutManager", "height =" + layoutManager.getHeight());
    }

    @Override
    public void logState(RecyclerView.State state) {
        Log.i("onLayoutChildren", "isPreLayout = " + state.isPreLayout());
    }
}
