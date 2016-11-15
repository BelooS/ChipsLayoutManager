package com.beloo.widget.chipslayoutmanager;

import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;

class ParcelableContainer implements Parcelable {

    private AnchorViewState anchorViewState;
    private int anchorPosition;
    private SparseArray<Object> orientationCacheMap = new SparseArray<>();
    private SparseArray<Object> cacheNormalizationPositionMap = new SparseArray<>();

    ParcelableContainer() {
        //initial values. We should normalize cache when scrolled to zero in case first time of changing orientation state
        cacheNormalizationPositionMap.put(Configuration.ORIENTATION_PORTRAIT, 0);
        cacheNormalizationPositionMap.put(Configuration.ORIENTATION_LANDSCAPE, 0);
    }

    void putAnchorViewState(AnchorViewState anchorViewState) {
        this.anchorViewState = anchorViewState;
    }

    AnchorViewState getAnchorViewState() {
        return anchorViewState;
    }

    public int getAnchorPosition() {
        return anchorPosition;
    }

    public void putAnchorPosition(int anchorPosition) {
        this.anchorPosition = anchorPosition;
    }

    @SuppressWarnings("unchecked")
    private ParcelableContainer(Parcel parcel) {
        anchorViewState = parcel.readParcelable(AnchorViewState.class.getClassLoader());
        orientationCacheMap = parcel.readSparseArray(Parcelable.class.getClassLoader());
        cacheNormalizationPositionMap = parcel.readSparseArray(Integer.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(anchorViewState, 0);
        parcel.writeSparseArray(orientationCacheMap);
        parcel.writeSparseArray(cacheNormalizationPositionMap);
    }

    void putPositionsCache(@DeviceOrientation int orientation, Parcelable parcelable) {
        orientationCacheMap.put(orientation, parcelable);
    }

    void putNormalizationPosition(@DeviceOrientation int orientation, @Nullable Integer normalizationPosition) {
        cacheNormalizationPositionMap.put(orientation, normalizationPosition);
    }

    @Nullable
    Parcelable getPositionsCache(@DeviceOrientation int orientation) {
        return (Parcelable) orientationCacheMap.get(orientation);
    }

    @IntRange(from = 0)
    @Nullable
    Integer getNormalizationPosition(@DeviceOrientation int orientation) {
        return (Integer) cacheNormalizationPositionMap.get(orientation);
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
