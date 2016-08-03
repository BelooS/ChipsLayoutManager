package com.beloo.widget.spanlayoutmanager;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @return View, which is highest visible left view
 */

class AnchorViewState {
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
}
