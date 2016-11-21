package com.beloo.widget.chipslayoutmanager;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Iterator;

public class ChildViewsIterable implements Iterable<View> {

    private RecyclerView.LayoutManager layoutManager;

    public ChildViewsIterable(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public Iterator<View> iterator() {
        return new Iterator<View>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < layoutManager.getChildCount();
            }

            @Override
            public View next() {
                return layoutManager.getChildAt(i++);
            }
        };
    }

    public int size() {
        return layoutManager.getChildCount();
    }
}
