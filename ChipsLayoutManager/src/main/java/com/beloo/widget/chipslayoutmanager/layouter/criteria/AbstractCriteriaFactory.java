package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import android.support.annotation.IntRange;

public abstract class AbstractCriteriaFactory implements ICriteriaFactory {
    int additionalHeight;
    int additionalRowCount;

    public void setAdditionalHeight(@IntRange(from = 0) int additionalHeight) {
        if (additionalHeight < 0) throw new IllegalArgumentException("additional height can't be negative");
        this.additionalHeight = additionalHeight;
    }

    public void setAdditionalRowsCount(int additionalRowCount) {
        this.additionalRowCount = additionalRowCount;
    }
}
