package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class RightGravityModifier extends ValidGravityModifier implements IGravityModifier {
    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect inputRect) {
        Rect childRect = super.modifyChildRect(minStart, maxEnd, inputRect);

        if (childRect.right < maxEnd) {
            childRect.left += maxEnd - childRect.right;
            childRect.right = maxEnd;
        }

        return childRect;
    }
}
