package com.beloo.widget.chipslayoutmanager.logger;

import android.util.Log;

public class AdapterActionsLogger implements IAdapterActionsLogger {
    @Override
    public void onItemsAdded(int positionStart, int itemCount) {
        Log.d("onItemsAdded",  "starts from = " + positionStart + ", item count = " + itemCount);
    }

    @Override
    public void onItemsRemoved(int positionStart, int itemCount) {
        Log.d("onItemsRemoved", "starts from = " + positionStart + ", item count = " + itemCount);
    }

    @Override
    public void onItemsChanged() {
        Log.d("onItemsChanged", "");
    }

    @Override
    public void onItemsUpdated(int positionStart, int itemCount) {
        Log.d("onItemsUpdated", "starts from = " + positionStart + ", item count = " + itemCount);
    }

    @Override
    public void onItemsMoved(int from, int to, int itemCount) {
        Log.d("onItemsMoved", "starts from " + from + " to " + to + ", item count = " + itemCount);
    }
}
