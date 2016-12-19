package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class CenterInColumnGravityModifier implements IGravityModifier {

    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        childRect = new Rect(childRect);

        int placeWidth = maxEnd - minStart;

        int rectWidth = childRect.right - childRect.left;
        int halfOffset = (placeWidth - rectWidth)/2;

        childRect.left = minStart + halfOffset;
        childRect.right = maxEnd - halfOffset;

        return childRect;
    }
}
