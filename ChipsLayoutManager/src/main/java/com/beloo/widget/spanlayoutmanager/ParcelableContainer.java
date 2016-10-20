package com.beloo.widget.spanlayoutmanager;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.SparseArray;

class ParcelableContainer implements Parcelable {

    private AnchorViewState anchorViewState;
    private SparseArray<Object> orientationCacheMap = new SparseArray<>();

    ParcelableContainer() {}

    void putAnchorViewState(AnchorViewState anchorViewState) {
        this.anchorViewState = anchorViewState;
    }

    AnchorViewState getAnchorViewState() {
        return anchorViewState;
    }

    @SuppressWarnings("unchecked")
    private ParcelableContainer(Parcel parcel) {
        anchorViewState = parcel.readParcelable(AnchorViewState.class.getClassLoader());
        orientationCacheMap = parcel.readSparseArray(Parcelable.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(anchorViewState, 0);
        parcel.writeSparseArray(orientationCacheMap);
    }

    void putPositionsCache(@DeviceOrientation int orientation, Parcelable parcelable) {
        orientationCacheMap.put(orientation, parcelable);
    }

    @Nullable
    Parcelable getPositionsCache(@DeviceOrientation int orientation) {
        return (Parcelable) orientationCacheMap.get(orientation);
    }

    public static final Creator<ParcelableContainer> CREATOR = new Creator<ParcelableContainer>() {

        @Override
        public ParcelableContainer createFromParcel(Parcel parcel) {
            return new ParcelableContainer(parcel);
        }

        @Override
        public ParcelableContainer[] newArray(int i) {
            return new ParcelableContainer[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
