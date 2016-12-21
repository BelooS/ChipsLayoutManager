package com.beloo.widget.chipslayoutmanager.cache;

import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

class ViewCacheStorage implements IViewCacheStorage {
    private static final String TAG = ViewCacheStorage.class.getSimpleName();
    private static final int SIZE_MAX_CACHE = 1000;

    private RecyclerView.LayoutManager layoutManager;
    private NavigableSet<Integer> startsRow = new TreeSet<>();
    private NavigableSet<Integer> endsRow = new TreeSet<>();
    private int maxCacheSize = SIZE_MAX_CACHE;
    private boolean isCachingEnabled;

    ViewCacheStorage(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        isCachingEnabled = true;
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    @Override
    public boolean isCachingEnabled() {
        return isCachingEnabled;
    }

    @Override
    public int getStartOfRow(int positionInRow) {
        Integer integer = startsRow.floor(positionInRow);
        if (integer == null) {
            integer = positionInRow;
        }
        return integer;
    }

    @Override
    public boolean isPositionEndsRow(int position) {
        return endsRow.contains(position);
    }

    @Override
    public boolean isPositionStartsRow(int position) {
        return startsRow.contains(position);
    }

    @Override
    public void setCachingEnabled(boolean isEnabled) {
        if (isCachingEnabled == isEnabled) return;
        Log.i(TAG, isEnabled ? "caching enabled" : "caching disabled");
        isCachingEnabled = isEnabled;
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
        if (isCachingEnabled && !row.isEmpty()) {

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
    public boolean isInCache(int position) {
        return startsRow.ceiling(position) != null || endsRow.ceiling(position) != null;
    }

    @Override
    public void purge() {
        startsRow.clear();
        endsRow.clear();
    }


    @Override
    public void purgeCacheToPosition(int position) {
        if (isCacheEmpty()) return;
        Log.d(TAG, "cache purged to position " + position);
        Iterator<Integer> removeIterator = startsRow.headSet(position).iterator();
        while (removeIterator.hasNext()) {
            removeIterator.next();
            removeIterator.remove();
        }

        removeIterator = endsRow.headSet(position).iterator();
        while (removeIterator.hasNext()) {
            removeIterator.next();
            removeIterator.remove();
        }
    }

    @Override

    public Integer getLastCachePosition() {
        if (isCacheEmpty()) return null;
        return endsRow.last();
    }

    @Override
    public boolean isCacheEmpty() {
        return endsRow.isEmpty();
    }


    @Override
    public void purgeCacheFromPosition(int position) {
        if (isCacheEmpty()) return;

        Iterator<Integer> removeIterator = startsRow.tailSet(position, true).iterator();
        while (removeIterator.hasNext()) {
            removeIterator.next();
            removeIterator.remove();
        }
        Integer previous = startsRow.lower(position);
        previous = previous == null? position : previous;

        //we should also remove previous end row cache to guarantee consistency
        removeIterator = endsRow.tailSet(previous, true).iterator();
        while (removeIterator.hasNext()) {
            removeIterator.next();
            removeIterator.remove();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return new CacheParcelableContainer(startsRow, endsRow);
    }

    public void onRestoreInstanceState(@Nullable Parcelable parcelable) {
        if (parcelable == null) return;
        if (!(parcelable instanceof CacheParcelableContainer)) throw new IllegalStateException("wrong parcelable passed");
        CacheParcelableContainer container = (CacheParcelableContainer) parcelable;
        startsRow = container.getStartsRow();
        endsRow = container.getEndsRow();
    }
}
