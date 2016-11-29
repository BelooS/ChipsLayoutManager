package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class TopGravityModifier implements IGravityModifier {

    @Override
    public void modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        if (childRect.top > minStart) {
            childRect.bottom -= (childRect.top - minStart);
            childRect.top = minStart;
        }
    }
}
