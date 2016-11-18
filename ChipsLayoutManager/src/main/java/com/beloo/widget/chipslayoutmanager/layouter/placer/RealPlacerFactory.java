package com.beloo.widget.chipslayoutmanager.layouter.placer;

import android.support.v7.widget.RecyclerView;

class RealPlacerFactory implements IPlacerFactory {

    private RecyclerView.LayoutManager layoutManager;

    RealPlacerFactory(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public IPlacer getAtStartPlacer() {
        return new RealAtStartPlacer(layoutManager);
    }

    @Override
    public IPlacer getAtEndPlacer() {
        return new RealAtEndPlacer(layoutManager);
    }
}
