package com.beloo.widget.chipslayoutmanager.layouter;

public class CriteriaDownLayouterFinished implements IFinishingCriteria {

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return abstractLayouter.getRowTop() > abstractLayouter.getCanvasBottomBorder();
    }
}
