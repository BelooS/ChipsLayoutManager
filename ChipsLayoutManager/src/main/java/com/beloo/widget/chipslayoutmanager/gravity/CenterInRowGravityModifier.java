package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class CenterInRowGravityModifier implements IGravityModifier {
    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        if (childRect.top < minStart) {
            throw new IllegalArgumentException("top point of input rect can't be lower than minTop");
        }
        if (childRect.bottom > maxEnd) {
            throw new IllegalArgumentException("bottom point of input rect can't be bigger than maxTop");
        }

        childRect = new Rect(childRect);

        int placeHeight = maxEnd - minStart;
        int rectHeight = childRect.bottom - childRect.top;
        //calculate needed offset
        int halfOffset = (placeHeight - rectHeight)/2;

        childRect.top = minStart + halfOffset;
        childRect.bottom = maxEnd - halfOffset;

        return childRect;
    }
}
