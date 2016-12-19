package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class TopGravityModifier extends ValidGravityModifier implements IGravityModifier {

    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect inputRect) {
        Rect childRect = super.modifyChildRect(minStart, maxEnd, inputRect);

        if (childRect.top > minStart) {
            childRect.bottom -= (childRect.top - minStart);
            childRect.top = minStart;
        }

        return childRect;
    }
}
