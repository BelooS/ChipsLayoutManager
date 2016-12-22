package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class TopGravityModifier implements IGravityModifier {

    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        if (childRect.top < minStart) {
            throw new IllegalArgumentException("top point of input rect can't be lower than minTop");
        }
        if (childRect.bottom > maxEnd) {
            throw new IllegalArgumentException("bottom point of input rect can't be bigger than maxTop");
        }

        childRect = new Rect(childRect);

        if (childRect.top > minStart) {
            childRect.bottom -= (childRect.top - minStart);
            childRect.top = minStart;
        }

        return childRect;
    }
}
