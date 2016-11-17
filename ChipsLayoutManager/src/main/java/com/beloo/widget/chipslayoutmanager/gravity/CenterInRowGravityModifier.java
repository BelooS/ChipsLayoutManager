package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class CenterInRowGravityModifier implements IGravityModifier {
    @Override
    public void modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        int placeHeight = maxEnd - minStart;
        int rectHeight = childRect.bottom - childRect.top;
        //calculate needed offset
        int halfOffset = (placeHeight - rectHeight)/2;

        childRect.top = minStart + halfOffset;
        childRect.bottom = maxEnd - halfOffset;
    }
}
