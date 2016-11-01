package com.beloo.widget.chipslayoutmanager.layouter;

public class CriteriaUpLayouterFinished implements IFinishingCriteria {

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return abstractLayouter.getRowBottom() < abstractLayouter.getCanvasTopBorder();
    }
}
