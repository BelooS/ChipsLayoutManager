package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class BottomGravityModifier implements IGravityModifier {
    @Override
    public void modifyChildRect(int minTop, int maxBottom, Rect childRect) {
        if (childRect.bottom < maxBottom) {
            childRect.top += maxBottom - childRect.bottom;
            childRect.bottom = maxBottom;
        }
    }
}
