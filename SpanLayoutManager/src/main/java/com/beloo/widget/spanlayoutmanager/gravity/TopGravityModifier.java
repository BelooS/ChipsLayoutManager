package com.beloo.widget.spanlayoutmanager.gravity;

import android.graphics.Rect;

class TopGravityModifier extends AbstractGravityModifier implements IGravityModifier {

    @Override
    public Rect modifyChildRect(int minTop, int maxBottom, Rect childRect) {
        childRect = super.modifyChildRect(minTop, maxBottom, childRect);
        if (childRect.top > minTop) {
            childRect.bottom -= (childRect.top - minTop);
            childRect.top = minTop;
        }
        return childRect;
    }
}
