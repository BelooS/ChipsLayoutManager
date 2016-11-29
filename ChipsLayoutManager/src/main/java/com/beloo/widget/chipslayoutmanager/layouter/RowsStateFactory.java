package com.beloo.widget.chipslayoutmanager.layouter;

import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.IScrollingController;
import com.beloo.widget.chipslayoutmanager.VerticalScrollingController;
import com.beloo.widget.chipslayoutmanager.anchor.IAnchorFactory;
import com.beloo.widget.chipslayoutmanager.anchor.RowsAnchorFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.AbstractCriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.ICriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.criteria.RowsCriteriaFactory;
import com.beloo.widget.chipslayoutmanager.layouter.placer.IPlacerFactory;

public class RowsStateFactory implements IStateFactory {

    private ChipsLayoutManager lm;
    private IOrientationStateFactory orientationStateFactory;

    public RowsStateFactory(ChipsLayoutManager lm) {
        this.lm = lm;
        orientationStateFactory = new RowsOrientationStateFactory(lm);
    }

    @Override
    public AbstractLayouterFactory createLayouterFactory(ICriteriaFactory criteriaFactory, IPlacerFactory placerFactory) {
        return orientationStateFactory.createLayouterFactory(criteriaFactory, placerFactory);
    }

    @Override
    public AbstractCriteriaFactory createDefaultFinishingCriteriaFactory() {
        return new RowsCriteriaFactory();
    }

    @Override
    public IAnchorFactory anchorFactory() {
        return new RowsAnchorFactory(lm, new Square(lm));
    }

    @Override
    public IScrollingController scrollingController() {
        return new VerticalScrollingController(lm);
    }

    @Override
    public int getStart(View view) {
        return lm.getDecoratedTop(view);
    }

    @Override
    public int getEnd(View view) {
        return lm.getDecoratedBottom(view);
    }

}
