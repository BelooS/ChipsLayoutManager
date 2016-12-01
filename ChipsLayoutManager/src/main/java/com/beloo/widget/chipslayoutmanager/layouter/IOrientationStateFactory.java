package com.beloo.widget.chipslayoutmanager.layouter;

import android.support.v7.widget.RecyclerView;

import com.beloo.widget.chipslayoutmanager.gravity.IRowStrategyFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.IBreakerFactory;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.IRowBreaker;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

interface IOrientationStateFactory {
    ILayouterCreator createLayouterCreator(RecyclerView.LayoutManager lm);
    IRowStrategyFactory createRowStrategyFactory();
    IBreakerFactory createDefaultBreaker();
}
