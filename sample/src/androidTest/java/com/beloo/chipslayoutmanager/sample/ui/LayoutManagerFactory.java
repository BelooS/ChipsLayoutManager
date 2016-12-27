package com.beloo.chipslayoutmanager.sample.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;

@VisibleForTesting
public class LayoutManagerFactory {
    @Nullable
    public RecyclerView.LayoutManager layoutManager(Context context) {
        return ChipsLayoutManager.newBuilder(context)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();
    }
}
