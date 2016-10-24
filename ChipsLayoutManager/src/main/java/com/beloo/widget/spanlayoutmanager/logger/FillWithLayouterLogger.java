package com.beloo.widget.spanlayoutmanager.logger;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;

public class FillWithLayouterLogger implements IFillWithLayouterLogger {
    private SparseArray<View> viewCache;
    private int requestedItems;
    private int recycledItems;
    private int startCacheSize;

    public FillWithLayouterLogger(SparseArray<View> viewCache) {
        this.viewCache = viewCache;
    }

    @Override
    public void onStart() {
        requestedItems = 0;
        recycledItems = 0;
        int startCacheSize = viewCache.size();

        Log.d("fillWithLayouter", "cached items = " + startCacheSize);
    }

    @Override
    public void onItemRequested() {
        requestedItems++;
    }

    @Override
    public void onItemRecycled() {
        recycledItems++;
    }

    @Override
    public void onFinishedLayouting() {
        Log.d("fillWithLayouter", "reattached items = " + (startCacheSize - viewCache.size() + " : requested items = " + requestedItems + " recycledItems = " + recycledItems));
    }
}
