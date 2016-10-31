package com.beloo.widget.chipslayoutmanager.util;

import android.os.Parcel;
import android.os.Parcelable;

public class EmptyParcelable implements Parcelable {

    public EmptyParcelable(){}

    protected EmptyParcelable(Parcel in) {}

    public static final Creator<EmptyParcelable> CREATOR = new Creator<EmptyParcelable>() {
        @Override
        public EmptyParcelable createFromParcel(Parcel in) {
            return new EmptyParcelable(in);
        }

        @Override
        public EmptyParcelable[] newArray(int size) {
            return new EmptyParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
