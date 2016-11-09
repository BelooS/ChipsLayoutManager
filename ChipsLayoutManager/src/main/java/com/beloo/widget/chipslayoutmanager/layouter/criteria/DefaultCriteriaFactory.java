package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import android.support.annotation.NonNull;

public class DefaultCriteriaFactory implements ICriteriaFactory {

    private int additionalHeight;

    public DefaultCriteriaFactory(int additionalHeight) {
        this.additionalHeight = additionalHeight;
    }

    @NonNull
    @Override
    public IFinishingCriteria getUpFinishingCriteria() {
        return new CriteriaUpAdditionalHeight(new CriteriaUpLayouterFinished(), additionalHeight);
    }

    @NonNull
    @Override
    public IFinishingCriteria getDownFinishingCriteria() {
        return new CriteriaDownAdditionalHeight(new CriteriaDownLayouterFinished(), additionalHeight);
    }
}
