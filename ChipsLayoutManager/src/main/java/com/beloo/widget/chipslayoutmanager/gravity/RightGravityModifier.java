package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

class RightGravityModifier extends PerpendicularItemGravity implements IGravityModifier {
    @Override
    public void modifyChildRect(AbstractLayouter layouter, Rect childRect) {
        super.modifyChildRect(layouter, childRect);
        if (childRect.right < maxEnd) {
            childRect.left += maxEnd - childRect.right;
            childRect.right = maxEnd;
        }
    }
}
