package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

public class CriteriaHorizontalBackwardLayouterFinished implements IFinishingCriteria {
    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return abstractLayouter.getViewRight() <= abstractLayouter.getCanvasLeftBorder();
    }
}
