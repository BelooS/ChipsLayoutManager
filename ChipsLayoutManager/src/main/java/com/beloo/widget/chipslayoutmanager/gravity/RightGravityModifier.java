package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

class RightGravityModifier implements IGravityModifier {
    @Override
    public void modifyChildRect(int minStart, int maxEnd, Rect childRect) {
        if (childRect.right < maxEnd) {
            childRect.left += maxEnd - childRect.right;
            childRect.right = maxEnd;
        }
    }
}
