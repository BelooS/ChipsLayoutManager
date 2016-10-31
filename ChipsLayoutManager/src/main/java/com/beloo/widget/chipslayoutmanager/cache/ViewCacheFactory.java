package com.beloo.widget.chipslayoutmanager.cache;

import android.support.v7.widget.RecyclerView;

public class ViewCacheFactory {

    private RecyclerView.LayoutManager layoutManager;

    public ViewCacheFactory(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public IViewCacheStorage createCacheStorage() {
        return new ViewCacheStorage(layoutManager);
    }
}
