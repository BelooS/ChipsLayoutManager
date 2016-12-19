package com.beloo.chipslayoumanager.sample.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;

class LayoutManagerFactory {
    RecyclerView.LayoutManager layoutManager(Context context) {
        return ChipsLayoutManager.newBuilder(context)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();
    }
}
