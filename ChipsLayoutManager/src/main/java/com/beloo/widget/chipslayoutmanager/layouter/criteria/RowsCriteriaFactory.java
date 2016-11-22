package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import android.support.annotation.NonNull;

public class RowsCriteriaFactory extends AbstractCriteriaFactory implements ICriteriaFactory {

    @NonNull
    @Override
    public IFinishingCriteria getBackwardFinishingCriteria() {
        IFinishingCriteria criteria = new CriteriaUpLayouterFinished();
        if (additionalLength != 0) {
            criteria = new CriteriaUpAdditionalHeight(criteria, additionalLength);
        }
        return criteria;
    }

    @NonNull
    @Override
    public IFinishingCriteria getForwardFinishingCriteria() {
        IFinishingCriteria criteria = new CriteriaDownLayouterFinished();
        if (additionalLength != 0) {
            criteria = new CriteriaDownAdditionalHeight(criteria, additionalLength);
        }
        if (additionalRowCount != 0) {
            criteria = new CriteriaAdditionalRow(criteria, additionalRowCount);
        }
        return criteria;
    }

}
