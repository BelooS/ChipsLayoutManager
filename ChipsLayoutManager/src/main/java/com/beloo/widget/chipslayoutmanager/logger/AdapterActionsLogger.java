package com.beloo.widget.chipslayoutmanager.logger;

import android.util.Log;

import timber.log.Timber;

public class AdapterActionsLogger implements IAdapterActionsLogger {
    @Override
    public void onItemsAdded(int positionStart, int itemCount) {
        Timber.d("onItemsAdded. starts from = " + positionStart + ", item count = " + itemCount);
    }

    @Override
    public void onItemsRemoved(int positionStart, int itemCount) {
        Timber.d("onItemsRemoved. starts from = " + positionStart + ", item count = " + itemCount);
    }

    @Override
    public void onItemsChanged() {
        Timber.d("onItemsChanged");
    }

    @Override
    public void onItemsUpdated(int positionStart, int itemCount) {
        Timber.d("onItemsUpdated. starts from = " + positionStart + ", item count = " + itemCount);
    }

    @Override
    public void onItemsMoved(int from, int to, int itemCount) {
        Timber.d("onItemsMoved. starts from " + from + " to " + to + ", item count = " + itemCount);
    }
}
