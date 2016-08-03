package com.beloo.widget.spanlayoutmanager.gravity;

import android.graphics.Rect;

class TopGravityModifier implements IGravityModifier {

    @Override
    public void modifyChildRect(int minTop, int maxBottom, Rect childRect) {
        if (childRect.top > minTop) {
            childRect.bottom -= (childRect.top - minTop);
            childRect.top = minTop;
        }
    }
}
