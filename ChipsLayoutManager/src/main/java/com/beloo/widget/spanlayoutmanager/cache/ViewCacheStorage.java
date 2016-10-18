package com.beloo.widget.spanlayoutmanager.cache;

import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;

import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

class ViewCacheStorage implements IViewCacheStorage {
    private static final int SIZE_MAX_CACHE = 1000;

    private RecyclerView.LayoutManager layoutManager;
    private NavigableSet<Integer> startsRow = new TreeSet<>();
    private NavigableSet<Integer> endsRow = new TreeSet<>();
    private int maxCacheSize = SIZE_MAX_CACHE;

    ViewCacheStorage(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    @Override
    public boolean isPositionEndsRow(int position) {
        return endsRow.contains(position);
    }

    @Override
    public boolean isPositionStartsRow(int position) {
        return startsRow.contains(position);
    }

    //todo test max size cache reached
    private void checkCacheSizeReached() {
        if (startsRow.size() > maxCacheSize) {
            startsRow.remove(startsRow.first());
        }
        if (endsRow.size() > maxCacheSize) {
            endsRow.remove(endsRow.first());
        }
    }

    @Override
    public void storeRow(List<Pair<Rect, View>> row) {
        if (!row.isEmpty()) {

            Pair<Rect, View> firstPair = row.get(0);
            Pair<Rect, View> secondPair = row.get(row.size()-1);

            int startPosition = layoutManager.getPosition(firstPair.second);
            int endPosition = layoutManager.getPosition(secondPair.second);

            checkCacheSizeReached();

            startsRow.add(startPosition);
            endsRow.add(endPosition);
        }
    }

    @Override
    public void purge() {
        throw new UnsupportedOperationException("not implemented");
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
