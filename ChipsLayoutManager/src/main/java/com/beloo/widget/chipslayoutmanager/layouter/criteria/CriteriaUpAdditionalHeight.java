package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

public class CriteriaUpAdditionalHeight extends FinishingCriteriaDecorator {

    private int additionalHeight;

    public CriteriaUpAdditionalHeight(IFinishingCriteria finishingCriteria, int additionalHeight) {
        super(finishingCriteria);
        this.additionalHeight = additionalHeight;
    }

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        int topBorder = abstractLayouter.getCanvasTopBorder();
        return super.isFinishedLayouting(abstractLayouter) &&
                //if additional height filled
                abstractLayouter.getViewBottom() < topBorder - additionalHeight;
    }

}
