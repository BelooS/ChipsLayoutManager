package com.beloo.widget.spanlayoutmanager.cache;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.NavigableSet;
import java.util.TreeSet;

final class ParcelableContainer implements Parcelable {
    private NavigableSet<Integer> startsRow = new TreeSet<>();
    private NavigableSet<Integer> endsRow = new TreeSet<>();

    ParcelableContainer(NavigableSet<Integer> startsRow, NavigableSet<Integer> endsRow) {
        this.startsRow = startsRow;
        this.endsRow = endsRow;
    }

    private ParcelableContainer(Parcel in) {
        Integer[] startsRowArray = (Integer[]) in.readArray(Integer.class.getClassLoader());
        Integer[] endsRowArray = (Integer[]) in.readArray(Integer.class.getClassLoader());

        startsRow = new TreeSet<>(Arrays.asList(startsRowArray));
        endsRow = new TreeSet<>(Arrays.asList(endsRowArray));
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Integer[] startsRowArray = new Integer[startsRow.size()];
        startsRow.toArray(startsRowArray);

        Integer[] endsRowArray = new Integer[endsRow.size()];
        endsRow.toArray(endsRowArray);

        parcel.writeArray(startsRowArray);
        parcel.writeArray(endsRowArray);
    }

    NavigableSet<Integer> getStartsRow() {
        return startsRow;
    }

    NavigableSet<Integer> getEndsRow() {
        return endsRow;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParcelableContainer> CREATOR = new Creator<ParcelableContainer>() {
        @Override
        public ParcelableContainer createFromParcel(Parcel in) {
            return new ParcelableContainer(in);
        }

        @Override
        public ParcelableContainer[] newArray(int size) {
            return new ParcelableContainer[size];
        }
    };
}
