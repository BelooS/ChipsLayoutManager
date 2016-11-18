package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import android.support.annotation.NonNull;

public class ColumnsCriteriaFactory extends AbstractCriteriaFactory {

    @NonNull
    @Override
    public IFinishingCriteria getBackwardFinishingCriteria() {
        return new CriteriaLeftLayouterFinished();
    }

    @NonNull
    @Override
    public IFinishingCriteria getForwardFinishingCriteria() {
        return new CriteriaRightLayouterFinished();
    }
}
