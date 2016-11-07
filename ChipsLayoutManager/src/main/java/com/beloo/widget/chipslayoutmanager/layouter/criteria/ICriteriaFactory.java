package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import android.support.annotation.NonNull;

public interface ICriteriaFactory {
    @NonNull
    IFinishingCriteria getUpFinishingCriteria();

    @NonNull
    IFinishingCriteria getDownFinishingCriteria();

}
