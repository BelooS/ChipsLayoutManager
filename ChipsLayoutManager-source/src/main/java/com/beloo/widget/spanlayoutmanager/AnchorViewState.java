package com.beloo.widget.spanlayoutmanager;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @return View, which is highest visible left view
 */

class AnchorViewState implements Parcelable {
    @Nullable
    private Integer position;
    @NonNull
    private Rect anchorViewRect;

    AnchorViewState() {
        anchorViewRect = new Rect(0, 0, 0, 0);
    }

    static AnchorViewState getNotFoundState() {
        return new AnchorViewState();
    }

    AnchorViewState(int position, @NonNull Rect anchorViewRect) {
        this.position = position;
        this.anchorViewRect = anchorViewRect;
    }

    boolean isNotFoundState() {
        return position == null;
    }

    public int getPosition() {
        return position;
    }

    public Rect getAnchorViewRect() {
        return anchorViewRect;
    }

    //parcelable logic below

    private AnchorViewState(Parcel parcel) {
        int parcelPosition = parcel.readInt();
        position = parcelPosition == -1? null : parcelPosition;
        anchorViewRect = parcel.readParcelable(AnchorViewState.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(position == null? -1 : position);
        parcel.writeParcelable(anchorViewRect, 0);
    }

    public static final Parcelable.Creator<AnchorViewState> CREATOR = new Parcelable.Creator<AnchorViewState>() {
        // unpack Object from Parcel
        public AnchorViewState createFromParcel(Parcel in) {
            return new AnchorViewState(in);
        }

        public AnchorViewState[] newArray(int size) {
            return new AnchorViewState[size];
        }
    };
}
