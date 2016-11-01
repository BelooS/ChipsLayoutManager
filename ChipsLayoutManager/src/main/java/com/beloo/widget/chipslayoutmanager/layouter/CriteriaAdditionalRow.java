package com.beloo.widget.chipslayoutmanager.layouter;

public class CriteriaAdditionalRow extends FinishingCriteriaDecorator implements IFinishingCriteria, ILayouterListener {

    private int requiredRowsCount;

    private boolean isListenerAttached;
    private int additionalRowsCount;

    public CriteriaAdditionalRow(IFinishingCriteria finishingCriteria, int requiredRowsCount) {
        super(finishingCriteria);
        this.requiredRowsCount = requiredRowsCount;
    }

    @Override
    public boolean isFinishedLayouting(AbstractLayouter abstractLayouter) {
        if (!isListenerAttached && super.isFinishedLayouting(abstractLayouter)) {
            isListenerAttached = true;
            abstractLayouter.addLayouterListener(this);
        }

        return super.isFinishedLayouting(abstractLayouter) && requiredRowsCount == additionalRowsCount;
    }

    @Override
    public void onLayoutRow(ILayouter layouter) {
        additionalRowsCount++;
    }
}
