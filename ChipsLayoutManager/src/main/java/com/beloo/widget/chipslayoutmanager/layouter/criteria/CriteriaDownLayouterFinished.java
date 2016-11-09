package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

public class CriteriaDownLayouterFinished implements IFinishingCriteria {

    private boolean isFinished;

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        isFinished = isFinished || abstractLayouter.getRowTop() > abstractLayouter.getCanvasBottomBorder();
        return isFinished;
    }

    @Override
    public boolean isFinishedLayouting() {
        return isFinished;
    }
}
