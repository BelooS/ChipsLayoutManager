package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import android.support.annotation.NonNull;

public class ColumnsCriteriaFactory extends AbstractCriteriaFactory {

    @SuppressWarnings("UnnecessaryLocalVariable")
    @NonNull
    @Override
    public IFinishingCriteria getBackwardFinishingCriteria() {
        IFinishingCriteria criteria = new CriteriaLeftLayouterFinished();
        if (additionalLength != 0) {
            criteria = new CriteriaLeftAdditionalWidth(criteria, additionalLength);
        }
        return criteria;
    }

    @NonNull
    @Override
    public IFinishingCriteria getForwardFinishingCriteria() {
        IFinishingCriteria criteria = new CriteriaRightLayouterFinished();
        if (additionalLength != 0) {
            criteria = new CriteriaRightAdditionalWidth(criteria, additionalLength);
        }
        if (additionalRowCount != 0) {
            criteria = new CriteriaAdditionalRow(criteria, additionalRowCount);
        }
        return criteria;
    }
}
