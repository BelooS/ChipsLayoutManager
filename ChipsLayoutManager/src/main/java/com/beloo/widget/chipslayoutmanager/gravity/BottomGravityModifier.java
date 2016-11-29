package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class BottomGravityModifier implements IGravityModifier {
    @Override
    public void modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        if (childRect.bottom < maxEnd) {
            childRect.top += maxEnd - childRect.bottom;
            childRect.bottom = maxEnd;
        }
    }
}
