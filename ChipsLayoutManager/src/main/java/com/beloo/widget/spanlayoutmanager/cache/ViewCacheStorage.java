package com.beloo.widget.spanlayoutmanager.cache;

import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;

import java.util.List;
import java.util.TreeSet;

class ViewCacheStorage implements IViewCacheStorage {

    private RecyclerView.LayoutManager layoutManager;
    private TreeSet<Integer> startsRow = new TreeSet<>();
    private TreeSet<Integer> endsRow = new TreeSet<>();

    ViewCacheStorage(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
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
    public void storeRow(List<Pair<Rect, View>> row) {
        if (!row.isEmpty()) {

            Pair<Rect, View> firstPair = row.get(0);
            Pair<Rect, View> secondPair = row.get(row.size()-1);

            int startPosition = layoutManager.getPosition(firstPair.second);
            int endPosition = layoutManager.getPosition(secondPair.second);

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
