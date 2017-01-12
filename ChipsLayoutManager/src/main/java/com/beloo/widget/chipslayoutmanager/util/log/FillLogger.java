package com.beloo.widget.chipslayoutmanager.util.log;

import android.util.SparseArray;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;

import java.util.Locale;

class FillLogger implements IFillLogger {
    private SparseArray<View> viewCache;
    private int requestedItems;
    private int recycledItems;
    private int startCacheSize;
    private int recycledSize;

    FillLogger(SparseArray<View> viewCache) {
        this.viewCache = viewCache;
    }

    @Override
    public void onStartLayouter(int startPosition) {
        requestedItems = 0;
        recycledItems = 0;
        startCacheSize = viewCache.size();

        Log.d("fillWithLayouter", "start position = " + startPosition, LogSwitcherFactory.FILL);
        Log.d("fillWithLayouter", "cached items = " + startCacheSize, LogSwitcherFactory.FILL);
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
        Log.d("fillWithLayouter",
                String.format(Locale.getDefault(), "reattached items = %d : requested items = %d recycledItems = %d", startCacheSize - viewCache.size(), requestedItems, recycledItems),
                LogSwitcherFactory.FILL);
    }

    @Override
    public void onAfterLayouter() {
        recycledSize = viewCache.size();
    }

    @Override
    public void onRemovedAndRecycled(int position) {
        Log.d("fillWithLayouter", " recycle position =" + viewCache.keyAt(position), LogSwitcherFactory.FILL);
        recycledSize++;
    }

    @Override
    public void onAfterRemovingViews() {
        Log.d("fillWithLayouter", "recycled count = " + recycledSize, LogSwitcherFactory.FILL);
    }

    @Override
    public void onBeforeLayouter(AnchorViewState anchorView) {
        if (anchorView.getAnchorViewRect() != null) {
            Log.d("fill",  "anchorPos " + anchorView.getPosition(), LogSwitcherFactory.FILL);
            Log.d("fill", "anchorTop " + anchorView.getAnchorViewRect().top, LogSwitcherFactory.FILL);
        }
    }

}
