package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import android.support.annotation.NonNull;

public class RowsCriteriaFactory extends AbstractCriteriaFactory implements ICriteriaFactory {

    @NonNull
    @Override
    public IFinishingCriteria getBackwardFinishingCriteria() {
        IFinishingCriteria criteria = new CriteriaUpLayouterFinished();
        if (additionalHeight != 0) {
            criteria = new CriteriaUpAdditionalHeight(criteria, additionalHeight);
        }
        return criteria;
    }

    @NonNull
    @Override
    public IFinishingCriteria getForwardFinishingCriteria() {
        IFinishingCriteria criteria = new CriteriaDownLayouterFinished();
        if (additionalHeight != 0) {
            criteria = new CriteriaUpAdditionalHeight(criteria, additionalHeight);
        }
        if (additionalRowCount != 0) {
            criteria = new CriteriaAdditionalRow(criteria, additionalRowCount);
        }
        return criteria;
    }

}
