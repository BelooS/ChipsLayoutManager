package com.beloo.widget.spanlayoutmanager;

import android.os.Parcel;
import android.os.Parcelable;

class SLMParcelableContainer implements Parcelable {

    private AnchorViewState anchorViewState;

    SLMParcelableContainer(AnchorViewState state) {
        anchorViewState = state;
    }

    public AnchorViewState getAnchorViewState() {
        return anchorViewState;
    }

    private SLMParcelableContainer(Parcel parcel) {
        anchorViewState = parcel.readParcelable(AnchorViewState.class.getClassLoader());
    }

    public static final Creator<SLMParcelableContainer> CREATOR = new Creator<SLMParcelableContainer>() {

        @Override
        public SLMParcelableContainer createFromParcel(Parcel parcel) {
            return new SLMParcelableContainer(parcel);
        }

        @Override
        public SLMParcelableContainer[] newArray(int i) {
            return new SLMParcelableContainer[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(anchorViewState, 0);
    }
}
