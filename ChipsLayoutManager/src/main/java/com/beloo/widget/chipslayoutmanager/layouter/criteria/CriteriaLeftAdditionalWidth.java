package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

class CriteriaLeftAdditionalWidth extends FinishingCriteriaDecorator {

    private int additionalWidth;

    CriteriaLeftAdditionalWidth(IFinishingCriteria finishingCriteria, int additionalWidth) {
        super(finishingCriteria);
        this.additionalWidth = additionalWidth;
    }

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        int leftBorder = abstractLayouter.getCanvasLeftBorder();
        return super.isFinishedLayouting(abstractLayouter) &&
                //if additional height filled
                abstractLayouter.getViewRight() < leftBorder - additionalWidth;
    }

}
