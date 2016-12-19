package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class TopGravityModifier implements IGravityModifier {

    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        childRect = new Rect(childRect);

        if (childRect.top > minStart) {
            childRect.bottom -= (childRect.top - minStart);
            childRect.top = minStart;
        }

        return childRect;
    }
}
