package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class CenterInRowGravityModifier extends ValidGravityModifier implements IGravityModifier {
    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect inputRect) {
        Rect childRect = super.modifyChildRect(minStart, maxEnd, inputRect);

        int placeHeight = maxEnd - minStart;
        int rectHeight = childRect.bottom - childRect.top;
        //calculate needed offset
        int halfOffset = (placeHeight - rectHeight)/2;

        childRect.top = minStart + halfOffset;
        childRect.bottom = maxEnd - halfOffset;

        return childRect;
    }
}
