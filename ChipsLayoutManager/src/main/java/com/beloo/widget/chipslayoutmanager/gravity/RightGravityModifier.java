package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class RightGravityModifier implements IGravityModifier {
    @Override
    public Rect modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        childRect = new Rect(childRect);

        if (childRect.right < maxEnd) {
            childRect.left += maxEnd - childRect.right;
            childRect.right = maxEnd;
        }

        return childRect;
    }
}
