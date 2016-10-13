package com.beloo.widget.spanlayoutmanager.cache;

import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.SparseArray;

class ViewCacheStorage implements IViewCacheStorage {

    private SparseArray<Rect> viewPositionRectArray;

    ViewCacheStorage() {
        viewPositionRectArray = new SparseArray<>();
    }

    @Nullable
    @Override
    public Rect getRect(int position) {
        return viewPositionRectArray.get(position);
    }

    @Override
    public void put(Rect rect, int position) {
        viewPositionRectArray.put(position, rect);
    }

    @Override
    public void purge() {
        viewPositionRectArray.clear();
    }

    @Override
    public void purgeCacheToPosition(int position) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void purgeCacheFromPosition(int position) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Parcelable persist() {
        throw new UnsupportedOperationException("not implemented");
    }
}
