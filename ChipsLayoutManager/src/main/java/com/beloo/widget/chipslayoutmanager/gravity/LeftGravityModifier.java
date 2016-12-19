package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class LeftGravityModifier extends ValidGravityModifier implements IGravityModifier {

    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect inputRect) {
        Rect childRect = super.modifyChildRect(minStart, maxEnd, inputRect);

        if (childRect.left > minStart) {
            childRect.right -= (childRect.left - minStart);
            childRect.left = minStart;
        }

        return childRect;
    }
}
