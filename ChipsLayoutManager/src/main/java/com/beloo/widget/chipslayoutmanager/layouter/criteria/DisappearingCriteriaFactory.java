package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import android.support.annotation.NonNull;

public class DisappearingCriteriaFactory implements ICriteriaFactory {

    private int additionalRowsCount;

    public DisappearingCriteriaFactory(int additionalRowsCount) {
        this.additionalRowsCount = additionalRowsCount;
    }

    @NonNull
    @Override
    public IFinishingCriteria getUpFinishingCriteria() {
        throw new UnsupportedOperationException("not implemented");
    }

    @NonNull
    @Override
    public IFinishingCriteria getDownFinishingCriteria() {
        return new CriteriaAdditionalRow(new EmtpyCriteria(), additionalRowsCount);
    }
}
