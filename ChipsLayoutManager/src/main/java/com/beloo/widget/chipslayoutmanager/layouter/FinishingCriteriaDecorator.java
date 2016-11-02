package com.beloo.widget.chipslayoutmanager.layouter;

abstract class FinishingCriteriaDecorator implements IFinishingCriteria {

    private IFinishingCriteria finishingCriteria;

    FinishingCriteriaDecorator(IFinishingCriteria finishingCriteria) {
        this.finishingCriteria = finishingCriteria;
    }

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return finishingCriteria.isFinishedLayouting(abstractLayouter);
    }
}
