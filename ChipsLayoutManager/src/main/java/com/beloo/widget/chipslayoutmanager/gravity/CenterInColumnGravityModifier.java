package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class CenterInColumnGravityModifier extends ValidGravityModifier implements IGravityModifier {

    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect inputRect) {
        Rect childRect = super.modifyChildRect(minStart, maxEnd, inputRect);
        int placeWidth = maxEnd - minStart;

        int rectWidth = childRect.right - childRect.left;
        int halfOffset = (placeWidth - rectWidth)/2;

        childRect.left = minStart + halfOffset;
        childRect.right = maxEnd - halfOffset;

        return childRect;
    }
}
