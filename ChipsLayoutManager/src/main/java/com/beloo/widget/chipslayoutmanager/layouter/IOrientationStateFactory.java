package com.beloo.widget.chipslayoutmanager.layouter;

import com.beloo.widget.chipslayoutmanager.RowStrategy;
import com.beloo.widget.chipslayoutmanager.gravity.IRowStrategyFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

interface IOrientationStateFactory {
    AbstractLayouterFactory createLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory);
}
