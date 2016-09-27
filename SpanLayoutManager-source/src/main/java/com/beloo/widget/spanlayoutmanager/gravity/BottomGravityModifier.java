package com.beloo.widget.spanlayoutmanager.gravity;

import android.graphics.Rect;

class BottomGravityModifier extends AbstractGravityModifier implements IGravityModifier {
    @Override
    public Rect modifyChildRect(int minTop, int maxBottom, Rect childRect) {
        childRect = super.modifyChildRect(minTop, maxBottom, childRect);
        if (childRect.bottom < maxBottom) {
            childRect.top += maxBottom - childRect.bottom;
            childRect.bottom = maxBottom;
        }
        return childRect;
    }
}
