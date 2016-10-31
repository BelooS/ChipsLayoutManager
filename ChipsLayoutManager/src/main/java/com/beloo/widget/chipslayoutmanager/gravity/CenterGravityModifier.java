package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class CenterGravityModifier implements IGravityModifier {
    @Override
    public void modifyChildRect(int minTop, int maxBottom, Rect childRect) {
        int placeHeight = maxBottom - minTop;
        int rectHeight = childRect.bottom - childRect.top;
        //calculate needed offset
        int halfOffset = (placeHeight - rectHeight)/2;

        childRect.top = minTop + halfOffset;
        childRect.bottom = maxBottom - halfOffset;
    }
}
