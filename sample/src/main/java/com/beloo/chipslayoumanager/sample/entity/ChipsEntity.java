package com.beloo.chipslayoumanager.sample.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ChipsEntity implements Parcelable {
    @DrawableRes
    private Integer drawableResId;

    @Nullable
    private String description;

    @NonNull
    private String name;

    public Integer getDrawableResId() {
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

    public void setName(@NonNull String name) {
        this.name = name;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.drawableResId);
        dest.writeString(this.description);
        dest.writeString(this.name);
    }

    protected ChipsEntity(Parcel in) {
        this.drawableResId = in.readInt();
        this.description = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<ChipsEntity> CREATOR = new Parcelable.Creator<ChipsEntity>() {
        @Override
        public ChipsEntity createFromParcel(Parcel source) {
            return new ChipsEntity(source);
        }

        @Override
        public ChipsEntity[] newArray(int size) {
            return new ChipsEntity[size];
        }
    };
}
