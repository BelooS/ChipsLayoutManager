package com.beloo.widget.chipslayoutmanager.layouter;

public class CriteriaAdditionalHeight extends FinishingCriteriaDecorator {

    private int additionalHeight;

    CriteriaAdditionalHeight(IFinishingCriteria finishingCriteria, int additionalHeight) {
        super(finishingCriteria);
        this.additionalHeight = additionalHeight;
    }

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        int bottomBorder = abstractLayouter.getCanvasBottomBorder();
        return super.isFinishedLayouting(abstractLayouter) &&
                //if additional height filled
                abstractLayouter.getRowTop() > bottomBorder + additionalHeight;
    }
}
