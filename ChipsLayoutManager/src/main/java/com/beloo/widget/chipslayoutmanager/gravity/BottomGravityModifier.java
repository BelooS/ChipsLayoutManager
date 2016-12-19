package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class BottomGravityModifier extends ValidGravityModifier implements IGravityModifier {
    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        Rect modified = super.modifyChildRect(minStart, maxEnd, childRect);
        if (modified.bottom < maxEnd) {
            modified.top += maxEnd - modified.bottom;
            modified.bottom = maxEnd;
        }
        return modified;
    }
}
