package com.beloo.widget.chipslayoutmanager.layouter;

public abstract class FinishingCriteriaDecorator implements IFinishingCriteria {

    private IFinishingCriteria finishingCriteria;

    public FinishingCriteriaDecorator(IFinishingCriteria finishingCriteria) {
        this.finishingCriteria = finishingCriteria;
    }

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        return finishingCriteria.isFinishedLayouting(abstractLayouter);
    }
}
