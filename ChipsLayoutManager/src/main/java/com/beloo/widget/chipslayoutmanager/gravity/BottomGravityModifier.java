package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class BottomGravityModifier implements IGravityModifier {
    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        if (childRect.top < minStart) {
            throw new IllegalArgumentException("top point of input rect can't be lower than minTop");
        }
        if (childRect.bottom > maxEnd) {
            throw new IllegalArgumentException("bottom point of input rect can't be bigger than maxTop");
        }
        Rect modified = new Rect(childRect);

        if (modified.bottom < maxEnd) {
            modified.top += maxEnd - modified.bottom;
            modified.bottom = maxEnd;
        }
        return modified;
    }
}
