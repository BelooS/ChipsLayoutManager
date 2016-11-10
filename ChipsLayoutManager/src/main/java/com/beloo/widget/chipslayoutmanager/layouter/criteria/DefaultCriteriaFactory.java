package com.beloo.widget.chipslayoutmanager.layouter.criteria;

import android.support.annotation.NonNull;

public class DefaultCriteriaFactory implements ICriteriaFactory {

    private int additionalHeight;
    private int additionalRowCount;

    private DefaultCriteriaFactory(Builder builder) {
        additionalHeight = builder.additionalHeight;
        additionalRowCount = builder.additionalRowCount;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @NonNull
    @Override
    public IFinishingCriteria getUpFinishingCriteria() {
        return new CriteriaUpAdditionalHeight(new CriteriaUpLayouterFinished(), additionalHeight);
    }

    @NonNull
    @Override
    public IFinishingCriteria getDownFinishingCriteria() {
        return new CriteriaAdditionalRow(new CriteriaDownAdditionalHeight(new CriteriaDownLayouterFinished(), additionalHeight), additionalRowCount);
    }


    public static final class Builder {
        private int additionalHeight;
        private int additionalRowCount;

        private Builder() {
        }

        @NonNull
        public Builder additionalHeight(int additionalHeight) {
            this.additionalHeight = additionalHeight;
            return this;
        }

        @NonNull
        public Builder additionalRowCount(int additionalRowCount) {
            this.additionalRowCount = additionalRowCount;
            return this;
        }

        @NonNull
        public DefaultCriteriaFactory build() {
            return new DefaultCriteriaFactory(this);
        }
    }
}
