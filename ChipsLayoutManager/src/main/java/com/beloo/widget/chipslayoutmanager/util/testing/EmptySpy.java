package com.beloo.widget.chipslayoutmanager.util.testing;

import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;

@VisibleForTesting
public class EmptySpy implements ISpy {
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //do nothing
    }
}
