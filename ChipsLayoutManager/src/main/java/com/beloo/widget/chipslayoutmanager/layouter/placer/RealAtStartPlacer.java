package com.beloo.widget.chipslayoutmanager.layouter.placer;

import android.support.v7.widget.RecyclerView;
import android.view.View;

class RealAtStartPlacer extends AbstractPlacer implements IPlacer {
    RealAtStartPlacer(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void addView(View view) {
        //mark that we add view at beginning of children
        getLayoutManager().addView(view, 0);
    }
}
