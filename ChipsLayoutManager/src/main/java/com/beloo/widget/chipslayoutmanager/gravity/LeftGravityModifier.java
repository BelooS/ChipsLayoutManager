package com.beloo.widget.chipslayoutmanager.gravity;

import android.graphics.Rect;

import com.beloo.widget.chipslayoutmanager.layouter.AbstractLayouter;

class LeftGravityModifier extends PerpendicularItemGravity implements IGravityModifier {

    @Override
    public void modifyChildRect(AbstractLayouter layouter, Rect childRect) {
        super.modifyChildRect(layouter, childRect);
        if (childRect.left > minStart) {
            childRect.right -= (childRect.left - minStart);
            childRect.left = minStart;
        }
    }
}
