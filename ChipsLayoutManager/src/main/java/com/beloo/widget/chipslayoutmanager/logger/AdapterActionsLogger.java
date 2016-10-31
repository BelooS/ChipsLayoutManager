package com.beloo.widget.chipslayoutmanager.logger;

import android.util.Log;

public class AdapterActionsLogger implements IAdapterActionsLogger {
    private static final String TAG = AdapterActionsLogger.class.getSimpleName();

    @Override
    public void onItemsAdded(int positionStart, int itemCount) {
        Log.d(TAG, "onItemsAdded. starts from = " + positionStart + ", item count = " + itemCount);
    }

    @Override
    public void onItemsRemoved(int positionStart, int itemCount) {
        Log.d(TAG, "onItemsRemoved. starts from = " + positionStart + ", item count = " + itemCount);
    }

    @Override
    public void onItemsChanged() {
        Log.d(TAG, "onItemsChanged");
    }

    @Override
    public void onItemsUpdated(int positionStart, int itemCount) {
        Log.d(TAG, "onItemsUpdated. starts from = " + positionStart + ", item count = " + itemCount);
    }

    @Override
    public void onItemsMoved(int from, int to, int itemCount) {
        Log.d(TAG, "onItemsMoved. starts from " + from + " to " + to + ", item count = " + itemCount);
    }
}
