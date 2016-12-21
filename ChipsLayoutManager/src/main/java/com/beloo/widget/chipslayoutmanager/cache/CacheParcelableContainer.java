package com.beloo.widget.chipslayoutmanager.cache;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

public final class CacheParcelableContainer implements Parcelable {
    private NavigableSet<Integer> startsRow = new TreeSet<>();
    private NavigableSet<Integer> endsRow = new TreeSet<>();

    CacheParcelableContainer(NavigableSet<Integer> startsRow, NavigableSet<Integer> endsRow) {
        this.startsRow = startsRow;
        this.endsRow = endsRow;
    }

    private CacheParcelableContainer(Parcel in) {
        List<Integer> startsRowList = new LinkedList<>();
        List<Integer> endsRowList = new LinkedList<>();
        in.readList(startsRowList, Integer.class.getClassLoader());
        in.readList(endsRowList, Integer.class.getClassLoader());

        startsRow = new TreeSet<>(startsRowList);
        endsRow = new TreeSet<>(endsRowList);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        List<Integer> startRowList = new LinkedList<>(startsRow);
        List<Integer> endRowList = new LinkedList<>(endsRow);

        parcel.writeList(startRowList);
        parcel.writeList(endRowList);
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

    public static final Creator<CacheParcelableContainer> CREATOR = new Creator<CacheParcelableContainer>() {
        @Override
        public CacheParcelableContainer createFromParcel(Parcel in) {
            return new CacheParcelableContainer(in);
        }

        @Override
        public CacheParcelableContainer[] newArray(int size) {
            return new CacheParcelableContainer[size];
        }
    };
}
