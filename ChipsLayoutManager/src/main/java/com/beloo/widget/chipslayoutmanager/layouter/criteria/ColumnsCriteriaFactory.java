package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import android.support.annotation.NonNull;

public class ColumnsCriteriaFactory extends AbstractCriteriaFactory {

    @SuppressWarnings("UnnecessaryLocalVariable")
    @NonNull
    @Override
    public IFinishingCriteria getBackwardFinishingCriteria() {
        IFinishingCriteria criteria = new CriteriaLeftLayouterFinished();
//        if (additionalRowCount != 0) {
//            criteria = new CriteriaAdditionalRow(criteria, additionalRowCount);
//        }
        return criteria;
    }

    @NonNull
    @Override
    public IFinishingCriteria getForwardFinishingCriteria() {
        IFinishingCriteria criteria = new CriteriaRightLayouterFinished();
        if (additionalRowCount != 0) {
            criteria = new CriteriaAdditionalRow(criteria, additionalRowCount);
        }
        return criteria;
    }
}
