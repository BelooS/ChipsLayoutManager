package com.beloo.widget.chipslayoutmanager.util.testing;

        import android.support.annotation.VisibleForTesting;
        import android.support.v7.widget.RecyclerView;

@VisibleForTesting
public interface ISpy {
    void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state);
}
