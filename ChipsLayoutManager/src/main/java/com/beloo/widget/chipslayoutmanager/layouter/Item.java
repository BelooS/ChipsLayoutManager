package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;

public class Item {
    private Rect viewRect;
    private int viewPosition;

    public Item(Rect viewRect, int viewPosition) {
        this.viewRect = viewRect;
        this.viewPosition = viewPosition;
    }

    public Rect getViewRect() {
        return viewRect;
    }

    public int getViewPosition() {
        return viewPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return viewPosition == item.viewPosition;

    }

    @Override
    public int hashCode() {
        return viewPosition;
    }
}
