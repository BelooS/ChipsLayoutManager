package beloo.recyclerviewcustomadapter.entity;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ChipsEntity {
    @DrawableRes
    private int drawableResId;

    @Nullable
    private String description;

    @NonNull
    private String name;

    public int getDrawableResId() {
        return drawableResId;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getName() {
        return name;
    }

    private ChipsEntity(Builder builder) {
        drawableResId = builder.drawableResId;
        description = builder.description;
        name = builder.name;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private int drawableResId;
        private String description;
        private String name;

        private Builder() {
        }

        @NonNull
        public Builder drawableResId(int drawableResId) {
            this.drawableResId = drawableResId;
            return this;
        }

        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public Builder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        @NonNull
        public ChipsEntity build() {
            return new ChipsEntity(this);
        }
    }
}
