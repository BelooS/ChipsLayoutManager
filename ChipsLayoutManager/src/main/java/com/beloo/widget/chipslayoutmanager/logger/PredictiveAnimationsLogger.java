package com.beloo.widget.chipslayoutmanager.logger;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import timber.log.Timber;

public class PredictiveAnimationsLogger implements IPredictiveAnimationsLogger {

    @Override
    public void onSummarizedDeletingItemsHeightCalculated(int additionalHeight) {
        Timber.d("onDeletingHeightCalc. " + "additional height  = " + additionalHeight);
    }

    @Override
    public void heightOfCanvas(RecyclerView.LayoutManager layoutManager) {
        Timber.d("LayoutManager. " + "height =" + layoutManager.getHeight());
    }

    @Override
    public void logState(RecyclerView.State state) {
        Timber.i("onLayoutChildren. " + "isPreLayout = " + state.isPreLayout());
    }
}
