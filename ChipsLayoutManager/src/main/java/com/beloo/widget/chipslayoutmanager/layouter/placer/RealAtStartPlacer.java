package com.beloo.widget.chipslayoutmanager.layouter.placer;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import timber.log.Timber;

class RealAtStartPlacer extends AbstractPlacer implements IPlacer {
    public RealAtStartPlacer(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void addView(View view) {
        //mark that we add view at beginning of children
        getLayoutManager().addView(view, 0);

        Timber.i("add view " +getLayoutManager().getPosition(view) + " at top");
    }
}
