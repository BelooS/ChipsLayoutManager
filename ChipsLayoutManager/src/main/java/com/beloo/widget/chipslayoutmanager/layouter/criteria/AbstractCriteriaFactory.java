package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import android.support.annotation.IntRange;

public abstract class AbstractCriteriaFactory implements ICriteriaFactory {
    int additionalLength;
    int additionalRowCount;

    public void setAdditionalLength(@IntRange(from = 0) int additionalHeight) {
        if (additionalHeight < 0) throw new IllegalArgumentException("additional height can't be negative");
        this.additionalLength = additionalHeight;
    }

    public void setAdditionalRowsCount(int additionalRowCount) {
        this.additionalRowCount = additionalRowCount;
    }
}
