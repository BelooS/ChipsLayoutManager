package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;
import com.beloo.widget.chipslayoutmanager.layouter.ILayouter;
import com.beloo.widget.chipslayoutmanager.layouter.ILayouterListener;

import timber.log.Timber;

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

        if (super.isFinishedLayouting(abstractLayouter) && additionalRowsCount >= requiredRowsCount) {
            Timber.d("finished additional row, child count = " + abstractLayouter.getLayoutManager().getChildCount());
        }
        return super.isFinishedLayouting(abstractLayouter) && additionalRowsCount >= requiredRowsCount;
    }

    @Override
    public void onLayoutRow(ILayouter layouter) {
        additionalRowsCount++;
    }
}
