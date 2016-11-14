package com.beloo.widget.chipslayoutmanager.layouter.placer;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import timber.log.Log;

class RealAtEndPlacer extends AbstractPlacer implements IPlacer {
    public RealAtEndPlacer(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void addView(View view) {
        getLayoutManager().addView(view);

//        Log.i("added view, position = " + getLayoutManager().getPosition(view));
    }
}
