package com.beloo.widget.chipslayoutmanager.logger;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.AnchorViewState;

public class FillLogger implements IFillLogger {
    private SparseArray<View> viewCache;
    private int requestedItems;
    private int recycledItems;
    private int startCacheSize;
    private int recycledSize;

    public FillLogger(SparseArray<View> viewCache) {
        this.viewCache = viewCache;
    }

    @Override
    public void onStartLayouter() {
        requestedItems = 0;
        recycledItems = 0;
        startCacheSize = viewCache.size();

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
    public void onFinishedLayouter() {
        Log.d("fillWithLayouter", "reattached items = " + (startCacheSize - viewCache.size() + " : requested items = " + requestedItems + " recycledItems = " + recycledItems));
    }

    @Override
    public void onAfterLayouter() {
        recycledSize = viewCache.size();
    }

    @Override
    public void onRemovedAndRecycled(int position) {
        Log.d("fillWithLayouter", "recycle position =" + viewCache.keyAt(position));
        recycledSize++;
    }

    @Override
    public void onAfterRemovingViews() {
        Log.d("fillWithLayouter", "recycled count = " + recycledSize);
    }

    @Override
    public void onBeforeLayouter(AnchorViewState anchorView) {
        if (anchorView.getAnchorViewRect() != null) {
            Log.d("fill", "anchorPos " + anchorView.getPosition());
            Log.d("fill", "anchorTop " + anchorView.getAnchorViewRect().top);
        }
    }
}
