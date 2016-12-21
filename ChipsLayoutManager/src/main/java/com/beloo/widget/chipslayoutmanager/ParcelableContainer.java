package com.beloo.widget.chipslayoutmanager;

import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.beloo.widget.chipslayoutmanager.anchor.AnchorViewState;
import com.beloo.widget.chipslayoutmanager.cache.CacheParcelableContainer;

class ParcelableContainer implements Parcelable {

    private AnchorViewState anchorViewState;
    private SparseArray<Object> orientationCacheMap = new SparseArray<>();
    private SparseArray<Object> cacheNormalizationPositionMap = new SparseArray<>();
    //store previous orientation
    private int orientation;

    ParcelableContainer() {
        //initial values. We should normalize cache when scrolled to zero in case first time of changing layoutOrientation state
        cacheNormalizationPositionMap.put(Configuration.ORIENTATION_PORTRAIT, 0);
        cacheNormalizationPositionMap.put(Configuration.ORIENTATION_LANDSCAPE, 0);
    }

    void putAnchorViewState(AnchorViewState anchorViewState) {
        this.anchorViewState = anchorViewState;
    }

    AnchorViewState getAnchorViewState() {
        return anchorViewState;
    }

    @DeviceOrientation
    int getOrientation() {
        return orientation;
    }

    void putOrientation(@DeviceOrientation int orientation) {
        this.orientation = orientation;
    }

    @SuppressWarnings("unchecked")
    private ParcelableContainer(Parcel parcel) {
        anchorViewState = AnchorViewState.CREATOR.createFromParcel(parcel);
        orientationCacheMap = parcel.readSparseArray(CacheParcelableContainer.class.getClassLoader());
        cacheNormalizationPositionMap = parcel.readSparseArray(Integer.class.getClassLoader());
        orientation = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        anchorViewState.writeToParcel(parcel, i);
        parcel.writeSparseArray(orientationCacheMap);
        parcel.writeSparseArray(cacheNormalizationPositionMap);
        parcel.writeInt(orientation);
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
